package com.salilvnair.intellij.plugin.daakia.persistence;

import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Simple SQLite database helper used by the plugin.
 */
public class DaakiaDatabase {
    private static final DaakiaDatabase INSTANCE = new DaakiaDatabase();
    private final String dbPath;
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private DaakiaDatabase() {
        String userHomePath = System.getProperty("user.home");
        File dir = new File(userHomePath + File.separator + ".salilvnair" + File.separator + "daakia");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.dbPath = dir.getAbsolutePath() + File.separator + "daakia.db";
        init();
    }

    public static DaakiaDatabase getInstance() {
        return INSTANCE;
    }

    private void init() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS history_records (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS collection_records (id INTEGER PRIMARY KEY, data TEXT)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS environment_records (id INTEGER PRIMARY KEY, data TEXT)");
        }
        catch (SQLException ignore) {}
        migrateIfNecessary();
    }

    public Connection getConnection() throws SQLException {
//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//        while (drivers.hasMoreElements()) {
//            System.out.println("Driver found: " + drivers.nextElement());
//        }
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
        catch (SQLException e) {
            // retry after forcing registration
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
    }

    private void migrateIfNecessary() {
        // migrate history if table empty and file exists
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM history_records");
            boolean historyEmpty = rs.next() && rs.getInt(1) == 0;
            rs.close();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM collection_records");
            boolean storeEmpty = rs.next() && rs.getInt(1) == 0;
            rs.close();
            if (historyEmpty) {
                File historyFile = DaakiaUtils.historyFile();
                if (historyFile.exists()) {
                    try {
                        String json = JsonUtils.readJsonFromFile(historyFile);
                        Map<String, List<DaakiaHistory>> data = JsonUtils.jsonToPojo(json, new TypeReference<>() {});
                        if (data != null) {
                            List<DaakiaHistory> flat = new ArrayList<>();
                            data.values().forEach(flat::addAll);
                            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO history_records(data) VALUES(?)")) {
                                for (DaakiaHistory h : flat) {
                                    ps.setString(1, JsonUtils.pojoToJson(h));
                                    ps.executeUpdate();
                                }
                            }
                        }
                    }
                    catch (Exception ignore) {}
                }
            }
            if (storeEmpty) {
                File storeFile = DaakiaUtils.storeFile();
                if (storeFile.exists()) {
                    try {
                        String json = JsonUtils.readJsonFromFile(storeFile);
                        DaakiaStore store = JsonUtils.jsonToPojo(json, DaakiaStore.class);
                        if (store != null) {
                            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO collection_records(id,data) VALUES(1,?)")) {
                                ps.setString(1, JsonUtils.pojoToJson(store));
                                ps.executeUpdate();
                            }
                        }
                    }
                    catch (Exception ignore) {}
                }
            }
        }
        catch (SQLException ignore) {}
    }
}
