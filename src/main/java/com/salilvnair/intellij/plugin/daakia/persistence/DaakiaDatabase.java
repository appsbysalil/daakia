package com.salilvnair.intellij.plugin.daakia.persistence;

import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * SQLite database helper for Daakia plugin (stateless + safer)
 */
public class DaakiaDatabase {

    private static final DaakiaDatabase INSTANCE = new DaakiaDatabase();
    private final String collectionDbPath;
    private final String historyDbPath;
    private final File dbDir;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found: " + e.getMessage());
        }
    }

    private DaakiaDatabase() {
        String userHomePath = System.getProperty("user.home");
        File dir = new File(userHomePath, ".salilvnair" + File.separator + "daakia");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.dbDir = dir;
        this.collectionDbPath = new File(dir, "collections.db").getAbsolutePath();
        this.historyDbPath = new File(dir, "history.db").getAbsolutePath();
        init();
    }

    public static DaakiaDatabase getInstance() {
        return INSTANCE;
    }

    private void init() {
        initHistoryDb();
        initCollectionDb();
        migrateFromLegacyDbIfNecessary();
        migrateHistoryIfNecessary();
        migrateStoreIfNecessary();
    }

    private void initHistoryDb() {
        try (Connection conn = getHistoryConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS history_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data TEXT,
                    active TEXT DEFAULT 'Y'
                )
            """);

            try {
                stmt.executeUpdate("ALTER TABLE history_records ADD COLUMN active TEXT DEFAULT 'Y'");
            } catch (SQLException ignore) {
                // column exists already
            }
        } catch (SQLException e) {
            System.err.println("Error initializing history database: " + e.getMessage());
        }
    }

    private void initCollectionDb() {
        try (Connection conn = getCollectionConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS collection_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    parent_id INTEGER,
                    name TEXT,
                    type TEXT,
                    active BOOLEAN DEFAULT 1,
                    collection_name TEXT,
                    url TEXT,
                    request_type TEXT,
                    headers TEXT,
                    response_headers TEXT,
                    request_body TEXT,
                    response_body TEXT,
                    pre_request_script TEXT,
                    post_request_script TEXT,
                    created_date TEXT,
                    size_text TEXT,
                    time_taken TEXT,
                    status_code INTEGER,
                    auth_info TEXT,
                    uuid TEXT UNIQUE
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS environment_records (
                    id INTEGER PRIMARY KEY,
                    data TEXT
                )
            """);

            try {
                stmt.executeUpdate("ALTER TABLE collection_records ADD COLUMN auth_info TEXT");
            } catch (SQLException ignore) {
                // column exists already
            }
        } catch (SQLException e) {
            System.err.println("Error initializing collection database: " + e.getMessage());
        }
    }

    public Connection getHistoryConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + historyDbPath);
    }

    public Connection getCollectionConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + collectionDbPath);
    }

    private void migrateFromLegacyDbIfNecessary() {
        File legacyDb = new File(dbDir, "daakia.db");
        if (!legacyDb.exists()) {
            return;
        }
        try (Connection legacyConn = DriverManager.getConnection("jdbc:sqlite:" + legacyDb.getAbsolutePath())) {
            migrateHistoryFromLegacy(legacyConn);
            migrateStoreFromLegacy(legacyConn);
        } catch (SQLException e) {
            System.err.println("Legacy DB migration failed: " + e.getMessage());
        }

        File backup = new File(dbDir, "daakia.db.bak");
        if (!legacyDb.renameTo(backup)) {
            // If rename fails, attempt delete to avoid repeated migration
            //noinspection ResultOfMethodCallIgnored
            legacyDb.delete();
        }
    }

    private void migrateHistoryFromLegacy(Connection legacyConn) {
        try (Connection historyConn = getHistoryConnection(); Statement stmt = historyConn.createStatement()) {
            if (!isEmpty(stmt, "history_records")) {
                return;
            }
            try (Statement oldStmt = legacyConn.createStatement();
                 ResultSet rs = oldStmt.executeQuery("SELECT id,data,active FROM history_records")) {
                String insert = "INSERT INTO history_records(id,data,active) VALUES(?,?,?)";
                try (PreparedStatement ps = historyConn.prepareStatement(insert)) {
                    while (rs.next()) {
                        ps.setInt(1, rs.getInt("id"));
                        ps.setString(2, rs.getString("data"));
                        ps.setString(3, rs.getString("active"));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error migrating legacy history: " + e.getMessage());
        }
    }

    private void migrateStoreFromLegacy(Connection legacyConn) {
        try (Connection collectionConn = getCollectionConnection()) {
            // migrate collection records
            try (Statement stmt = collectionConn.createStatement()) {
                if (isEmpty(stmt, "collection_records")) {
                    try (Statement oldStmt = legacyConn.createStatement();
                         ResultSet rs = oldStmt.executeQuery("SELECT id,parent_id,name,type,active,collection_name,url,request_type,headers,response_headers,request_body,response_body,pre_request_script,post_request_script,created_date,size_text,time_taken,status_code,auth_info,uuid FROM collection_records")) {
                        String insert = "INSERT INTO collection_records(id,parent_id,name,type,active,collection_name,url,request_type,headers,response_headers,request_body,response_body,pre_request_script,post_request_script,created_date,size_text,time_taken,status_code,auth_info,uuid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        try (PreparedStatement ps = collectionConn.prepareStatement(insert)) {
                            while (rs.next()) {
                                ps.setInt(1, rs.getInt("id"));
                                ps.setObject(2, rs.getObject("parent_id"));
                                ps.setString(3, rs.getString("name"));
                                ps.setString(4, rs.getString("type"));
                                ps.setObject(5, rs.getObject("active"));
                                ps.setString(6, rs.getString("collection_name"));
                                ps.setString(7, rs.getString("url"));
                                ps.setString(8, rs.getString("request_type"));
                                ps.setString(9, rs.getString("headers"));
                                ps.setString(10, rs.getString("response_headers"));
                                ps.setString(11, rs.getString("request_body"));
                                ps.setString(12, rs.getString("response_body"));
                                ps.setString(13, rs.getString("pre_request_script"));
                                ps.setString(14, rs.getString("post_request_script"));
                                ps.setString(15, rs.getString("created_date"));
                                ps.setString(16, rs.getString("size_text"));
                                ps.setString(17, rs.getString("time_taken"));
                                ps.setObject(18, rs.getObject("status_code"));
                                String authInfo;
                                try {
                                    authInfo = rs.getString("auth_info");
                                } catch (SQLException ex) {
                                    authInfo = null;
                                }
                                ps.setString(19, authInfo);
                                ps.setString(20, rs.getString("uuid"));
                                ps.addBatch();
                            }
                            ps.executeBatch();
                        }
                    }
                }
            }

            // migrate environment records
            try (Statement stmt = collectionConn.createStatement()) {
                if (isEmpty(stmt, "environment_records")) {
                    try (Statement oldStmt = legacyConn.createStatement();
                         ResultSet rs = oldStmt.executeQuery("SELECT id,data FROM environment_records")) {
                        String insertEnv = "INSERT INTO environment_records(id,data) VALUES(?,?)";
                        try (PreparedStatement ps = collectionConn.prepareStatement(insertEnv)) {
                            while (rs.next()) {
                                ps.setInt(1, rs.getInt("id"));
                                ps.setString(2, rs.getString("data"));
                                ps.addBatch();
                            }
                            ps.executeBatch();
                        }
                    } catch (SQLException ignored) {
                        // legacy DB might not have environment table
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error migrating legacy store: " + e.getMessage());
        }
    }

    private void migrateHistoryIfNecessary() {
        try (Connection conn = getHistoryConnection(); Statement stmt = conn.createStatement()) {
            if (isEmpty(stmt, "history_records")) {
                migrateHistory(conn);
            }
        } catch (SQLException e) {
            System.err.println("History migration failed: " + e.getMessage());
        }
    }

    private void migrateStoreIfNecessary() {
        try (Connection conn = getCollectionConnection(); Statement stmt = conn.createStatement()) {
            if (isEmpty(stmt, "collection_records")) {
                migrateStore(conn);
            }
        } catch (SQLException e) {
            System.err.println("Store migration failed: " + e.getMessage());
        }
    }

    private boolean isEmpty(Statement stmt, String table) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private void migrateHistory(Connection conn) {
        File historyFile = DaakiaUtils.historyFile();
        if (!historyFile.exists()) return;

        try {
            String json = JsonUtils.readJsonFromFile(historyFile);
            Map<String, List<DaakiaHistory>> data = JsonUtils.jsonToPojo(json, new TypeReference<>() {});
            if (data == null || data.isEmpty()) return;

            List<DaakiaHistory> flat = new ArrayList<>();
            data.values().forEach(flat::addAll);

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO history_records(data) VALUES(?)")) {
                for (DaakiaHistory h : flat) {
                    ps.setString(1, JsonUtils.pojoToJson(h));
                    ps.executeUpdate();
                }
            }

            System.out.println("[Daakia] Migrated history records: " + flat.size());

        } catch (Exception e) {
            System.err.println("Error migrating history: " + e.getMessage());
        }
    }

    private void migrateStore(Connection conn) {
        File storeFile = DaakiaUtils.storeFile();
        if (!storeFile.exists()) return;

        try {
            String json = JsonUtils.readJsonFromFile(storeFile);
            DaakiaStore store = JsonUtils.jsonToPojo(json, DaakiaStore.class);
            if (store == null) return;

            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            DaakiaUtils.convertCollectionStoreToTreeNode(store, root);

            DbTreeStoreService treeService = new DbTreeStoreService();
            treeService.saveTree(conn, root);

            System.out.println("[Daakia] Migrated store to DB successfully.");

        } catch (Exception e) {
            System.err.println("Error migrating store: " + e.getMessage());
        }
    }
}
