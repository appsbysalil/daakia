package com.salilvnair.intellij.plugin.daakia.persistence;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreCollection;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DbTreeStoreService {

    // ============================
    // SAVE TREE (Stateless)
    // ============================
    public void saveTree(Connection connection, DefaultMutableTreeNode rootNode) throws SQLException {
        connection.setAutoCommit(false);

        String sql = """
            INSERT INTO collection_records (
                parent_id, name, type, active, collection_name, url, request_type, headers, response_headers,
                request_body, response_body, pre_request_script, post_request_script,
                created_date, size_text, time_taken, status_code, uuid
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(uuid) DO UPDATE SET
                parent_id=excluded.parent_id, name=excluded.name, type=excluded.type, active=excluded.active,
                collection_name=excluded.collection_name, url=excluded.url, request_type=excluded.request_type,
                headers=excluded.headers, response_headers=excluded.response_headers, request_body=excluded.request_body,
                response_body=excluded.response_body, pre_request_script=excluded.pre_request_script,
                post_request_script=excluded.post_request_script, created_date=excluded.created_date,
                size_text=excluded.size_text, time_taken=excluded.time_taken, status_code=excluded.status_code
            RETURNING id
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            saveNodeRecursive(connection, rootNode, null, ps);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void saveNodeRecursive(Connection connection,
                                   DefaultMutableTreeNode node,
                                   Integer parentId,
                                   PreparedStatement ps) throws SQLException {
        Object userObj = node.getUserObject();
        String type = "ROOT";
        boolean active = true;
        String name = "";
        String collectionName = null;
        String url = null, requestType = null, headers = null, responseHeaders = null;
        String requestBody = null, responseBody = null, preScript = null, postScript = null;
        String createdDate = null, sizeText = null, timeTaken = null;
        int statusCode = 0;
        String uuid;

        if (userObj instanceof DaakiaStoreCollection coll) {
            type = "COLLECTION";
            active = coll.isActive();
            name = coll.getCollectionName();
            collectionName = coll.getCollectionName();
            uuid = ensureUuid(coll);
        } else if (userObj instanceof DaakiaStoreRecord rec) {
            type = "RECORD";
            active = rec.isActive();
            name = rec.getDisplayName();
            url = rec.getUrl();
            requestType = rec.getRequestType();
            headers = rec.getHeaders();
            responseHeaders = rec.getResponseHeaders();
            requestBody = rec.getRequestBody();
            responseBody = rec.getResponseBody();
            preScript = rec.getPreRequestScript();
            postScript = rec.getPostRequestScript();
            createdDate = rec.getCreatedDate();
            sizeText = rec.getSizeText();
            timeTaken = rec.getTimeTaken();
            statusCode = rec.getStatusCode();
            uuid = ensureUuid(rec);
        } else {
            uuid = "ROOT"; // stable root ID
        }

        // Fill statement
        if (parentId != null) ps.setInt(1, parentId); else ps.setNull(1, Types.INTEGER);
        ps.setString(2, name);
        ps.setString(3, type);
        ps.setBoolean(4, active);
        ps.setString(5, collectionName);
        ps.setString(6, url);
        ps.setString(7, requestType);
        ps.setString(8, headers);
        ps.setString(9, responseHeaders);
        ps.setString(10, requestBody);
        ps.setString(11, responseBody);
        ps.setString(12, preScript);
        ps.setString(13, postScript);
        ps.setString(14, createdDate);
        ps.setString(15, sizeText);
        ps.setString(16, timeTaken);
        ps.setInt(17, statusCode);
        ps.setString(18, uuid);

        Integer currentId = null;
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                currentId = rs.getInt(1);
            }
        }

        // Save children
        for (int i = 0; i < node.getChildCount(); i++) {
            saveNodeRecursive(connection, (DefaultMutableTreeNode) node.getChildAt(i), currentId, ps);
        }
    }

    private String ensureUuid(DaakiaBaseStoreData base) {
        if (base.getUuid() == null || base.getUuid().isEmpty() || "0".equals(base.getUuid())) {
            base.setUuid(UUID.randomUUID().toString());
        }
        return base.getUuid();
    }

    // ============================
    // LOAD TREE (Stateless)
    // ============================
    public DefaultMutableTreeNode loadTree(Connection connection, boolean onlyActive) throws SQLException {
        Map<Integer, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        Map<Integer, Integer> parentMap = new HashMap<>();

        String sql = "SELECT * FROM collection_records " +
                (onlyActive ? "WHERE active = 1 OR type='ROOT' " : "") +
                "ORDER BY parent_id ASC, id ASC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Integer parentId = rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null;
                String type = rs.getString("type");
                String uuid = rs.getString("uuid");

                DefaultMutableTreeNode node = new DefaultMutableTreeNode();

                if ("COLLECTION".equalsIgnoreCase(type)) {
                    DaakiaStoreCollection coll = new DaakiaStoreCollection();
                    coll.setCollectionName(rs.getString("collection_name"));
                    coll.setActive(rs.getBoolean("active"));
                    coll.setUuid(uuid);
                    node.setUserObject(coll);
                } else if ("RECORD".equalsIgnoreCase(type)) {
                    DaakiaStoreRecord rec = new DaakiaStoreRecord();
                    rec.setDisplayName(rs.getString("name"));
                    rec.setUrl(rs.getString("url"));
                    rec.setRequestType(rs.getString("request_type"));
                    rec.setHeaders(rs.getString("headers"));
                    rec.setResponseHeaders(rs.getString("response_headers"));
                    rec.setRequestBody(rs.getString("request_body"));
                    rec.setResponseBody(rs.getString("response_body"));
                    rec.setPreRequestScript(rs.getString("pre_request_script"));
                    rec.setPostRequestScript(rs.getString("post_request_script"));
                    rec.setCreatedDate(rs.getString("created_date"));
                    rec.setSizeText(rs.getString("size_text"));
                    rec.setTimeTaken(rs.getString("time_taken"));
                    rec.setStatusCode(rs.getInt("status_code"));
                    rec.setActive(rs.getBoolean("active"));
                    rec.setUuid(uuid);
                    node.setUserObject(rec);
                } else {
                    node.setUserObject("Root");
                }

                nodeMap.put(id, node);
                parentMap.put(id, parentId);
            }
        }

        // Build hierarchy
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        for (Map.Entry<Integer, DefaultMutableTreeNode> entry : nodeMap.entrySet()) {
            Integer id = entry.getKey();
            DefaultMutableTreeNode node = entry.getValue();
            Integer parentId = parentMap.get(id);
            if (parentId == null) {
                root = node;
            } else {
                DefaultMutableTreeNode parentNode = nodeMap.get(parentId);
                if (parentNode != null) parentNode.add(node);
            }
        }
        return root;
    }

    // ============================
    // NODE OPERATIONS (Stateless)
    // ============================
    public void markNodeInactive(Connection connection, String uuid) throws SQLException {
        String sql = """
            WITH RECURSIVE descendants(id) AS (
                SELECT id FROM collection_records WHERE uuid = ?
                UNION ALL
                SELECT c.id FROM collection_records c
                JOIN descendants d ON c.parent_id = d.id
            )
            UPDATE collection_records SET active = 0 WHERE id IN (SELECT id FROM descendants)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        }
    }

    public void markNodeActive(Connection connection, String uuid) throws SQLException {
        String sql = """
            WITH RECURSIVE ancestors(id) AS (
                SELECT parent_id FROM collection_records WHERE uuid = ?
                UNION ALL
                SELECT c.parent_id FROM collection_records c
                JOIN ancestors a ON c.id = a.id
            ),
            descendants(id) AS (
                SELECT id FROM collection_records WHERE uuid = ?
                UNION ALL
                SELECT c.id FROM collection_records c
                JOIN descendants d ON c.parent_id = d.id
            )
            UPDATE collection_records SET active = 1
            WHERE id IN (SELECT id FROM descendants)
               OR id IN (SELECT id FROM ancestors WHERE id IS NOT NULL)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, uuid);
            ps.executeUpdate();
        }
    }

    public void deleteNode(Connection connection, String uuid) throws SQLException {
        String sql = """
            WITH RECURSIVE descendants(id) AS (
                SELECT id FROM collection_records WHERE uuid = ?
                UNION ALL
                SELECT c.id FROM collection_records c
                JOIN descendants d ON c.parent_id = d.id
            )
            DELETE FROM collection_records WHERE id IN (SELECT id FROM descendants)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        }
    }
}
