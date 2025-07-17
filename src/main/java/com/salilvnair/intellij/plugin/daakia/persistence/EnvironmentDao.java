package com.salilvnair.intellij.plugin.daakia.persistence;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO for environment records */
public class EnvironmentDao {

    public List<Environment> loadEnvironments() {
        List<Environment> list = new ArrayList<>();
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id,data FROM environment_records");
            while (rs.next()) {
                String json = rs.getString("data");
                try {
                    Environment env = JsonUtils.jsonToPojo(json, Environment.class);
                    env.setId(rs.getInt("id"));
                    list.add(env);
                } catch (Exception ignore) {}
            }
        } catch (SQLException ignore) {}
        return list;
    }

    public void saveEnvironments(List<Environment> envs) {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM environment_records");
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO environment_records(id,data) VALUES(?,?)")) {
                int id = 1;
                for (Environment env : envs) {
                    env.setId(id);
                    ps.setInt(1, id);
                    ps.setString(2, JsonUtils.pojoToJson(env));
                    ps.executeUpdate();
                    id++;
                }
            }
        } catch (Exception ignore) {}
    }
}
