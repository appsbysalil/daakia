package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer.CollectionStoreTreeCellRenderer;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;
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
    private JPanel collectionStoreTreePanel;

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
        collectionStoreTreePanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("History", new JBScrollPane(historyTrashTree));
        tabbedPane.addTab("Collection", collectionStoreTreePanel);
    }

    @Override
    public void initChildrenLayout() {
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        loadTrashData();
        listenGlobal(event -> {
            if (DaakiaEvent.ofType(event, DaakiaEventType.ON_REFRESH_TRASH_PANEL)) {
                loadTrashData();
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

        new CollectionDao().loadStoreAsync(dataContext, false, defaultMutableTreeNode -> {
            // Callback to handle after loading store
            dynamicTree(defaultMutableTreeNode, collectionStoreTreePanel);
        });
    }


    public void dynamicTree(DefaultMutableTreeNode defaultMutableTreeNode, JPanel collectionStoreTreePanel) {
        collectionTrashTree.setModel(new DefaultTreeModel(defaultMutableTreeNode));
        collectionTrashTree.setCellRenderer(new CollectionStoreTreeCellRenderer());
        collectionTrashTree.setOpaque(false);
        collectionTrashTree.setBackground(UIUtil.getTreeBackground());
        JScrollPane scrollPane = new JBScrollPane(collectionTrashTree);


        collectionStoreTreePanel.add(scrollPane, BorderLayout.CENTER);
        // Hide the root node
        collectionTrashTree.setRootVisible(false);

        // Expand all nodes in the collectionStoreTree
        TreeUtils.expandAllNodes(collectionTrashTree);
        DefaultTreeModel collectionTrashTreeModel = (DefaultTreeModel) collectionTrashTree.getModel();
        dataContext.sideNavContext().setCollectionStoreRootNode(defaultMutableTreeNode);
        collectionTrashTreeModel.reload();
    }
}
