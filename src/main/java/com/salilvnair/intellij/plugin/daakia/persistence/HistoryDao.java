package com.salilvnair.intellij.plugin.daakia.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** DAO for history records */
public class HistoryDao {

    public Map<String, List<DaakiaHistory>> loadHistory() {
        List<DaakiaHistory> list = new ArrayList<>();
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id, data FROM history_records WHERE active='Y'");
            while (rs.next()) {
                String json = rs.getString("data");
                try {
                    DaakiaHistory h = JsonUtils.jsonToPojo(json, DaakiaHistory.class);
                    h.setId(rs.getInt("id"));
                    list.add(h);
                } catch (Exception ignore) {}
            }
        } catch (SQLException ignore) {}
        return list.stream()
                .collect(Collectors.groupingBy(DaakiaHistory::getCreatedDate, LinkedHashMap::new, Collectors.toList()));
    }

    public List<DaakiaHistory> loadInactiveHistory() {
        List<DaakiaHistory> list = new ArrayList<>();
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id, data FROM history_records WHERE active='N'");
            while (rs.next()) {
                String json = rs.getString("data");
                try {
                    DaakiaHistory h = JsonUtils.jsonToPojo(json, DaakiaHistory.class);
                    h.setId(rs.getInt("id"));
                    list.add(h);
                } catch (Exception ignore) {}
            }
        } catch (SQLException ignore) {}
        return list;
    }

    public void saveHistory(Map<String, List<DaakiaHistory>> data) {
        List<DaakiaHistory> flat = new ArrayList<>();
        data.values().forEach(flat::addAll);
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM history_records WHERE active='Y'");
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO history_records(data,active) VALUES(?,?)")) {
                for (DaakiaHistory h : flat) {
                    try {
                        ps.setString(1, JsonUtils.pojoToJson(h));
                        ps.setString(2, "Y");
                        ps.executeUpdate();
                    } catch (Exception ignore) {}
                }
            }
        } catch (SQLException ignore) {}
    }

    public void markActive(int id, boolean active) {
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE history_records SET active=? WHERE id=?")) {
            ps.setString(1, active ? "Y" : "N");
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException ignore) {}
    }

    public void loadHistoryAsync(Consumer<Map<String, List<DaakiaHistory>>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            Map<String, List<DaakiaHistory>> data = loadHistory();
            if (callback != null) {
                ApplicationManager.getApplication().invokeLater(() -> callback.accept(data));
            }
        });
    }

    public void saveHistoryAsync(Map<String, List<DaakiaHistory>> data) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            saveHistory(data);
        });
    }

    public void markActiveAsync(int id, boolean active) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> markActive(id, active));
    }



}
