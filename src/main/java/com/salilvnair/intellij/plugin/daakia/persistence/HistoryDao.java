package com.salilvnair.intellij.plugin.daakia.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;

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
            ResultSet rs = stmt.executeQuery("SELECT id,display_name,request_type,url,headers,response_headers,request_body,response_body,pre_request_script,post_request_script,created_date,size_text,time_taken,status_code,auth_info,uuid FROM history_records WHERE active='Y'");
            while (rs.next()) {
                DaakiaHistory h = new DaakiaHistory();
                h.setId(rs.getInt("id"));
                h.setDisplayName(rs.getString("display_name"));
                h.setRequestType(rs.getString("request_type"));
                h.setUrl(rs.getString("url"));
                h.setHeaders(rs.getString("headers"));
                h.setResponseHeaders(rs.getString("response_headers"));
                h.setRequestBody(rs.getString("request_body"));
                h.setResponseBody(rs.getString("response_body"));
                h.setPreRequestScript(rs.getString("pre_request_script"));
                h.setPostRequestScript(rs.getString("post_request_script"));
                h.setCreatedDate(rs.getString("created_date"));
                h.setSizeText(rs.getString("size_text"));
                h.setTimeTaken(rs.getString("time_taken"));
                h.setStatusCode(rs.getInt("status_code"));
                h.setAuthInfo(rs.getString("auth_info"));
                h.setUuid(rs.getString("uuid"));
                list.add(h);
            }
        } catch (SQLException ignore) {}
        return list.stream()
                .collect(Collectors.groupingBy(DaakiaHistory::getCreatedDate, LinkedHashMap::new, Collectors.toList()));
    }

    public List<DaakiaHistory> loadInactiveHistory() {
        List<DaakiaHistory> list = new ArrayList<>();
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id,display_name,request_type,url,headers,response_headers,request_body,response_body,pre_request_script,post_request_script,created_date,size_text,time_taken,status_code,auth_info,uuid FROM history_records WHERE active='N'");
            while (rs.next()) {
                DaakiaHistory h = new DaakiaHistory();
                h.setId(rs.getInt("id"));
                h.setDisplayName(rs.getString("display_name"));
                h.setRequestType(rs.getString("request_type"));
                h.setUrl(rs.getString("url"));
                h.setHeaders(rs.getString("headers"));
                h.setResponseHeaders(rs.getString("response_headers"));
                h.setRequestBody(rs.getString("request_body"));
                h.setResponseBody(rs.getString("response_body"));
                h.setPreRequestScript(rs.getString("pre_request_script"));
                h.setPostRequestScript(rs.getString("post_request_script"));
                h.setCreatedDate(rs.getString("created_date"));
                h.setSizeText(rs.getString("size_text"));
                h.setTimeTaken(rs.getString("time_taken"));
                h.setStatusCode(rs.getInt("status_code"));
                h.setAuthInfo(rs.getString("auth_info"));
                h.setUuid(rs.getString("uuid"));
                list.add(h);
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
            String insert = "INSERT INTO history_records(display_name,request_type,url,headers,response_headers,request_body,response_body,pre_request_script,post_request_script,created_date,size_text,time_taken,status_code,auth_info,uuid,active) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                for (DaakiaHistory h : flat) {
                    ps.setString(1, h.getDisplayName());
                    ps.setString(2, h.getRequestType());
                    ps.setString(3, h.getUrl());
                    ps.setString(4, h.getHeaders());
                    ps.setString(5, h.getResponseHeaders());
                    ps.setString(6, h.getRequestBody());
                    ps.setString(7, h.getResponseBody());
                    ps.setString(8, h.getPreRequestScript());
                    ps.setString(9, h.getPostRequestScript());
                    ps.setString(10, h.getCreatedDate());
                    ps.setString(11, h.getSizeText());
                    ps.setString(12, h.getTimeTaken());
                    ps.setObject(13, h.getStatusCode());
                    ps.setString(14, h.getAuthInfo());
                    ps.setString(15, h.getUuid());
                    ps.setString(16, "Y");
                    ps.executeUpdate();
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


    // ============================
    // LAZY LOAD SUPPORT
    // ============================

    /**
     * Return distinct years for which history records exist.
     */
    public List<String> loadYears() {
        List<String> years = new ArrayList<>();
        String sql = "SELECT DISTINCT substr(created_date,1,4) AS yr FROM history_records WHERE active='Y' ORDER BY yr DESC";
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                years.add(rs.getString("yr"));
            }
        } catch (SQLException ignore) {}
        return years;
    }

    /**
     * Return distinct months (01-12) for a given year.
     */
    public List<String> loadMonths(String year) {
        List<String> months = new ArrayList<>();
        String sql = "SELECT DISTINCT substr(created_date,6,2) AS mn FROM history_records WHERE active='Y' AND substr(created_date,1,4)=? ORDER BY mn DESC";
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    months.add(rs.getString("mn"));
                }
            }
        } catch (SQLException ignore) {}
        return months;
    }

    /**
     * Load history records for a given year and month.
     */
    public List<DaakiaHistory> loadByMonth(String year, String month) {
        List<DaakiaHistory> list = new ArrayList<>();
        String sql = "SELECT id,display_name,request_type,url,headers,response_headers,request_body,response_body,pre_request_script,post_request_script,created_date,size_text,time_taken,status_code,auth_info,uuid FROM history_records WHERE active='Y' AND substr(created_date,1,4)=? AND substr(created_date,6,2)=? ORDER BY created_date DESC";
        try (Connection conn = DaakiaDatabase.getInstance().getHistoryConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, year);
            ps.setString(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DaakiaHistory h = new DaakiaHistory();
                    h.setId(rs.getInt("id"));
                    h.setDisplayName(rs.getString("display_name"));
                    h.setRequestType(rs.getString("request_type"));
                    h.setUrl(rs.getString("url"));
                    h.setHeaders(rs.getString("headers"));
                    h.setResponseHeaders(rs.getString("response_headers"));
                    h.setRequestBody(rs.getString("request_body"));
                    h.setResponseBody(rs.getString("response_body"));
                    h.setPreRequestScript(rs.getString("pre_request_script"));
                    h.setPostRequestScript(rs.getString("post_request_script"));
                    h.setCreatedDate(rs.getString("created_date"));
                    h.setSizeText(rs.getString("size_text"));
                    h.setTimeTaken(rs.getString("time_taken"));
                    h.setStatusCode(rs.getInt("status_code"));
                    h.setAuthInfo(rs.getString("auth_info"));
                    h.setUuid(rs.getString("uuid"));
                    list.add(h);
                }
            }
        } catch (SQLException ignore) {}
        return list;
    }

    public void loadYearsAsync(Consumer<List<String>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<String> data = loadYears();
            if (callback != null) {
                ApplicationManager.getApplication().invokeLater(() -> callback.accept(data));
            }
        });
    }

    public void loadMonthsAsync(String year, Consumer<List<String>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<String> data = loadMonths(year);
            if (callback != null) {
                ApplicationManager.getApplication().invokeLater(() -> callback.accept(data));
            }
        });
    }

    public void loadByMonthAsync(String year, String month, Consumer<List<DaakiaHistory>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<DaakiaHistory> data = loadByMonth(year, month);
            if (callback != null) {
                ApplicationManager.getApplication().invokeLater(() -> callback.accept(data));
            }
        });
    }


}
