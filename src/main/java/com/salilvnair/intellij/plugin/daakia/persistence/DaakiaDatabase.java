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

        migrateHistoryIfNecessary();
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

        migrateStoreIfNecessary();
    }

    public Connection getHistoryConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + historyDbPath);
    }

    public Connection getCollectionConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + collectionDbPath);
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
