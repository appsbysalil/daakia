package com.salilvnair.intellij.plugin.daakia.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

/** DAO for collection/store records (stateless + async-safe) */
public class CollectionDao {

    private final DbTreeStoreService treeStoreService = new DbTreeStoreService();

    // ============================
    // LOAD STORE (Active/Inactive)
    // ============================
    public DaakiaStore loadStore(boolean active) {
        String sql = "SELECT data FROM collection_records WHERE id=1 AND active=?";
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, active ? "Y" : "N");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String json = rs.getString("data");
                return JsonUtils.jsonToPojo(json, DaakiaStore.class);
            }
        } catch (Exception e) {
            System.err.println("Error loading store: " + e.getMessage());
        }
        return null;
    }

    public DaakiaStore loadStore() {
        return loadStore(true);
    }

    public DaakiaStore loadInactiveStore() {
        return loadStore(false);
    }

    // ============================
    // SAVE STORE (Legacy JSON mode)
    // ============================
    public void saveStore(DaakiaStore store) {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
            boolean exists;
            try (PreparedStatement check = conn.prepareStatement(
                    "SELECT COUNT(*) FROM collection_records WHERE id=1")) {
                ResultSet rs = check.executeQuery();
                exists = rs.next() && rs.getInt(1) > 0;
            }
            String json = JsonUtils.pojoToJson(store);
            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE collection_records SET data=?, active='Y' WHERE id=1")) {
                    ps.setString(1, json);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO collection_records(id,data,active) VALUES(1,?, 'Y')")) {
                    ps.setString(1, json);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving store JSON: " + e.getMessage());
        }
    }

    // ============================
    // SAVE TREE (New Structured Mode)
    // ============================
    public void saveStoreNew(DataContext dataContext) {
        DefaultMutableTreeNode root = dataContext.sideNavContext().collectionStoreRootNode();
        try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
            treeStoreService.saveTree(conn, root);
        } catch (Exception e) {
            System.err.println("Error saving store tree: " + e.getMessage());
        }
    }

    public void saveStoreAsync(DataContext dataContext) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> saveStoreNew(dataContext));
    }

    // ============================
    // LOAD TREE (Async)
    // ============================
    public void loadStoreAsync(DataContext dataContext, boolean onlyActive, Consumer<DefaultMutableTreeNode> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
                DefaultMutableTreeNode root = treeStoreService.loadTree(conn, onlyActive);

                if (onlyActive) {
                    dataContext.sideNavContext().setCollectionStoreRootNode(root);
                }

                if (callback != null) {
                    ApplicationManager.getApplication().invokeLater(() -> callback.accept(root));
                }
            } catch (SQLException e) {
                System.err.println("Error loading store tree: " + e.getMessage());
            }
        });
    }

    public void loadStoreAsync(DataContext dataContext, Consumer<DefaultMutableTreeNode> callback) {
        loadStoreAsync(dataContext, true, callback);
    }

    // ============================
    // NODE STATE OPERATIONS (Async)
    // ============================
    public void markNodeInactiveAsync(String uuid, Runnable onComplete) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
                treeStoreService.markNodeInactive(conn, uuid);
            } catch (SQLException e) {
                System.err.println("Error marking node inactive: " + e.getMessage());
            }
            if (onComplete != null) {
                ApplicationManager.getApplication().invokeLater(onComplete);
            }
        });
    }

    public void markNodeInactiveAsync(String uuid) {
        markNodeInactiveAsync(uuid, null);
    }

    public void markNodeActiveAsync(String uuid, Runnable onComplete) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
                treeStoreService.markNodeActive(conn, uuid);
            } catch (SQLException e) {
                System.err.println("Error marking node active: " + e.getMessage());
            }
            if (onComplete != null) {
                ApplicationManager.getApplication().invokeLater(onComplete);
            }
        });
    }

    public void deleteNodeAsync(String uuid, Runnable onComplete) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
                treeStoreService.deleteNode(conn, uuid);
            } catch (SQLException e) {
                System.err.println("Error deleting node: " + e.getMessage());
            }
            if (onComplete != null) {
                ApplicationManager.getApplication().invokeLater(onComplete);
            }
        });
    }

    // ============================
    // MARK ROOT ACTIVE/INACTIVE
    // ============================
    public void markActive(boolean active) {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE collection_records SET active=? WHERE id=1")) {
            ps.setString(1, active ? "Y" : "N");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating active flag: " + e.getMessage());
        }
    }

    public void markActiveAsync(boolean active) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> markActive(active));
    }
}
