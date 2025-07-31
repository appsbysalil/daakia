package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

/**
 * Panel displaying deleted history and collection entries.
 */
public class TrashPanel extends BaseDaakiaPanel<TrashPanel> {
    private JBTabbedPane tabbedPane;
    private Tree historyTrashTree;
    private Tree collectionTrashTree;

    public TrashPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        tabbedPane = new JBTabbedPane();
        historyTrashTree = new Tree();
        collectionTrashTree = new Tree();
        tabbedPane.addTab("History", new JBScrollPane(historyTrashTree));
        tabbedPane.addTab("Collection", new JBScrollPane(collectionTrashTree));
    }

    @Override
    public void initChildrenLayout() {
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        loadTrashData();
        historyTrashTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    Object obj = TreeUtils.extractSelectedNodeUserObject(historyTrashTree, e);
                    if(obj instanceof DaakiaHistory history) {
                        showHistoryPopup(e.getComponent(), e.getX(), e.getY(), history);
                    }
                }
            }
        });

        collectionTrashTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    Object obj = TreeUtils.extractSelectedNodeUserObject(collectionTrashTree, e);
                    if(obj instanceof DaakiaStore store) {
                        showCollectionPopup(e.getComponent(), e.getX(), e.getY(), store.getUuid());
                    }
                    else if(obj instanceof DaakiaStoreRecord rec) {
                        showCollectionPopup(e.getComponent(), e.getX(), e.getY(), rec.getUuid());
                    }
                }
            }
        });
    }

    private void loadTrashData() {
        List<DaakiaHistory> deletedHistory = new HistoryDao().loadInactiveHistory();
        DefaultMutableTreeNode historyRoot = new DefaultMutableTreeNode("History");
        for (DaakiaHistory h : deletedHistory) {
            historyRoot.add(new DefaultMutableTreeNode(h));
        }
        historyTrashTree.setModel(new DefaultTreeModel(historyRoot));
        TreeUtils.expandAllNodes(historyTrashTree);

        DaakiaStore store = new CollectionDao().loadStore();
        DefaultMutableTreeNode colRoot = new DefaultMutableTreeNode("Collections");
        if(store != null) {
            DaakiaUtils.convertCollectionStoreToTreeNodeOnlyInactive(store, colRoot);
        }
        collectionTrashTree.setModel(new DefaultTreeModel(colRoot));
        collectionTrashTree.setRootVisible(false);
        TreeUtils.expandAllNodes(collectionTrashTree);
    }

    private void showHistoryPopup(Component c, int x, int y, DaakiaHistory history) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem restore = new JMenuItem("Restore");
        restore.addActionListener(e -> restoreHistory(history));
        menu.add(restore);
        menu.show(c, x, y);
    }

    private void restoreHistory(DaakiaHistory history) {
        globalEventPublisher().onRestoreHistoryNode(history);
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_HISTORY, dataContext);
        loadTrashData();
    }

    private void showCollectionPopup(Component c, int x, int y, String uuid) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem restore = new JMenuItem("Restore");
        restore.addActionListener(e -> restoreCollection(uuid));
        menu.add(restore);
        menu.show(c, x, y);
    }

    private void restoreCollection(String uuid) {
        DaakiaStore rootStore = sideNavContext().daakiaStore();
        DaakiaUtils.updateActiveStatusByUuid(rootStore, uuid, true);
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_STORE_COLLECTIONS, dataContext);
        globalEventPublisher().onRestoreCollections();
        loadTrashData();
    }
}
