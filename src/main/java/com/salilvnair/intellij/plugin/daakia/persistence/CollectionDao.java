package com.salilvnair.intellij.plugin.daakia.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/** DAO for collection/store records (stateless + async-safe) */
public class CollectionDao {

    private final DbTreeStoreService treeStoreService = new DbTreeStoreService();

    // ============================
    // SAVE TREE (New Structured Mode)
    // ============================
    public void saveStore(DataContext dataContext) {
        DefaultMutableTreeNode root = dataContext.sideNavContext().collectionStoreRootNode();
        try (Connection conn = DaakiaDatabase.getInstance().getCollectionConnection()) {
            treeStoreService.saveTree(conn, root);
        } catch (Exception e) {
            System.err.println("Error saving store tree: " + e.getMessage());
        }
    }

    public void saveStoreAsync(DataContext dataContext) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> saveStore(dataContext));
    }

    // ============================
    // LOAD TREE (Async)
    // ============================
    public void loadStoreAsync(DataContext dataContext, boolean onlyActive, Consumer<DefaultMutableTreeNode> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getCollectionConnection()) {
                DefaultMutableTreeNode root = treeStoreService.loadRoot(conn, onlyActive);

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

    public void loadChildrenAsync(String parentUuid, Consumer<List<DefaultMutableTreeNode>> callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getCollectionConnection()) {
                List<DefaultMutableTreeNode> children = treeStoreService.loadChildren(conn, parentUuid, true);
                if (callback != null) {
                    ApplicationManager.getApplication().invokeLater(() -> callback.accept(children));
                }
            } catch (SQLException e) {
                System.err.println("Error loading child nodes: " + e.getMessage());
            }
        });
    }

    // ============================
    // NODE STATE OPERATIONS (Async)
    // ============================
    public void markNodeInactiveAsync(String uuid, Runnable onComplete) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try (Connection conn = DaakiaDatabase.getInstance().getCollectionConnection()) {
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
            try (Connection conn = DaakiaDatabase.getInstance().getCollectionConnection()) {
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
            try (Connection conn = DaakiaDatabase.getInstance().getCollectionConnection()) {
                treeStoreService.deleteNode(conn, uuid);
            } catch (SQLException e) {
                System.err.println("Error deleting node: " + e.getMessage());
            }
            if (onComplete != null) {
                ApplicationManager.getApplication().invokeLater(onComplete);
            }
        });
    }
}
