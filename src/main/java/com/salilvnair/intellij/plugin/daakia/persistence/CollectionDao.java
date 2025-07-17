package com.salilvnair.intellij.plugin.daakia.persistence;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;

import java.sql.*;

/** DAO for collection/store records */
public class CollectionDao {

    public DaakiaStore loadStore() {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT data FROM collection_records WHERE id=1")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String json = rs.getString("data");
                try {
                    return JsonUtils.jsonToPojo(json, DaakiaStore.class);
                } catch (Exception ignore) {}
            }
        } catch (SQLException ignore) {}
        return null;
    }

    public void saveStore(DaakiaStore store) {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO collection_records(id,data) VALUES(1,?) ON CONFLICT(id) DO UPDATE SET data=excluded.data")) {
            ps.setString(1, JsonUtils.pojoToJson(store));
            ps.executeUpdate();
        } catch (Exception ignore) {}
    }
}
