package com.salilvnair.intellij.plugin.daakia.persistence;

import com.intellij.openapi.application.ApplicationManager;
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
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT data FROM history_records");
            while (rs.next()) {
                String json = rs.getString("data");
                try {
                    DaakiaHistory h = JsonUtils.jsonToPojo(json, DaakiaHistory.class);
                    list.add(h);
                } catch (Exception ignore) {}
            }
        } catch (SQLException ignore) {}
        return list.stream()
                .collect(Collectors.groupingBy(DaakiaHistory::getCreatedDate, LinkedHashMap::new, Collectors.toList()));
    }

    public void saveHistory(Map<String, List<DaakiaHistory>> data) {
        List<DaakiaHistory> flat = new ArrayList<>();
        data.values().forEach(flat::addAll);
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM history_records");
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO history_records(data) VALUES(?)")) {
                for (DaakiaHistory h : flat) {
                    try {
                        ps.setString(1, JsonUtils.pojoToJson(h));
                        ps.executeUpdate();
                    } catch (Exception ignore) {}
                }
            }
        } catch (SQLException ignore) {}
    }

    public void loadHistoryAsync(Consumer<Map<String, List<DaakiaHistory>>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            Map<String, List<DaakiaHistory>> data = loadHistory();
            if (callback != null) {
                callback.accept(data);
            }
        });
    }

    public void saveHistoryAsync(Map<String, List<DaakiaHistory>> data) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            saveHistory(data);
        });
    }



}
