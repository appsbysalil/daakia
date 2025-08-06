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
    private final String dbPath;

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
        this.dbPath = new File(dir, "daakia.db").getAbsolutePath();
        init();
    }

    public static DaakiaDatabase getInstance() {
        return INSTANCE;
    }

    private void init() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS history_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data TEXT,
                    active TEXT DEFAULT 'Y'
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS collection_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    parent_id INTEGER,
                    name TEXT,
                    type TEXT,             -- 'COLLECTION' or 'RECORD'
                    active BOOLEAN DEFAULT 1,
                    collection_name TEXT,   -- if collection
                    url TEXT,               -- if record
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
                    uuid TEXT UNIQUE
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS environment_records (
                    id INTEGER PRIMARY KEY,
                    data TEXT
                )
            """);

            // Try adding missing column for older DBs
            try {
                stmt.executeUpdate("ALTER TABLE history_records ADD COLUMN active TEXT DEFAULT 'Y'");
            } catch (SQLException ignore) {
                // column exists already
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }

        migrateIfNecessary();
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
    }

    /**
     * Migration for old JSON-based storage to DB tree structure
     */
    private void migrateIfNecessary() {
        try (Connection conn = getConnection()) {
            boolean historyEmpty;
            boolean storeEmpty;

            try (Statement stmt = conn.createStatement()) {
                historyEmpty = isEmpty(stmt, "history_records");
                storeEmpty = isEmpty(stmt, "collection_records");
            }

            if (historyEmpty) {
                migrateHistory(conn);
            }
            if (storeEmpty) {
                migrateStore(conn);
            }

        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
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
