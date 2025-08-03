package com.salilvnair.intellij.plugin.daakia.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.*;

/** DAO for collection/store records */
public class CollectionDao {

    private DbTreeStoreService dbTreeStoreService;

    private volatile DbTreeStoreService treeStoreService;

    public DbTreeStoreService dbTreeStoreService(Connection conn) {
        if (treeStoreService == null) {
            synchronized (this) {
                if (treeStoreService == null) {
                    treeStoreService = new DbTreeStoreService(conn);
                }
            }
        }
        return treeStoreService;
    }


    public DaakiaStore loadStore() {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT data FROM collection_records WHERE id=1 AND active='Y'")) {
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

    public DaakiaStore loadInactiveStore() {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT data FROM collection_records WHERE id=1 AND active='N'")) {
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

    public void saveStoreNew(DataContext dataContext) {
        DefaultMutableTreeNode root = dataContext.sideNavContext().collectionStoreRootNode();
        try(Connection conn = DaakiaDatabase.getInstance().getConnection()) {
            dbTreeStoreService(conn).saveTree(root);
        }
        catch (Exception e) {
            System.out.println("Error saving store tree: " + e.getMessage());
        }
    }

    public void saveStore(DaakiaStore store) {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
            boolean exists;
            try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM collection_records WHERE id=1")) {
                ResultSet rs = check.executeQuery();
                exists = rs.next() && rs.getInt(1) > 0;
            }
            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE collection_records SET data=?, active=? WHERE id=1")) {
                    ps.setString(1, JsonUtils.pojoToJson(store));
                    ps.setString(2,  "Y");
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO collection_records(id,data,active) VALUES(1,?, 'Y')")) {
                    ps.setString(1, JsonUtils.pojoToJson(store));
                    ps.executeUpdate();
                }
            }
        } catch (Exception ignore) {}
    }

    public void markActive(boolean active) {
        try (Connection conn = DaakiaDatabase.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE collection_records SET active=? WHERE id=1");
            ps.setString(1, active ? "Y" : "N");
            ps.executeUpdate();
        } catch (SQLException ignore) {}
    }

    public void markActiveAsync(boolean active) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> markActive(active));
    }

    public void loadStoreAsync(DataContext dataContext, java.util.function.Consumer<DefaultMutableTreeNode> callback) {
        loadStoreAsync(dataContext, true, callback);
    }

    public void loadStoreAsync(DataContext dataContext, boolean onlyActive, java.util.function.Consumer<DefaultMutableTreeNode> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try(Connection conn = DaakiaDatabase.getInstance().getConnection()) {
                DefaultMutableTreeNode root = dbTreeStoreService(conn).loadTree(onlyActive);

                // Cache into context only when active tree is requested
                if (onlyActive) {
                    dataContext.sideNavContext().setCollectionStoreRootNode(root);
                }

                if (callback != null) {
                    ApplicationManager.getApplication().invokeLater(() -> callback.accept(root));
                }
            }
            catch (SQLException e) {
                System.out.println("Error loading store tree: " + e.getMessage());
            }
        });
    }


    public void saveStoreAsync(DataContext dataContext) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
//            saveStore(store);
            saveStoreNew(dataContext);
        });
    }
}