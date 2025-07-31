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
    }

    private void loadTrashData() {
        List<DaakiaHistory> deletedHistory = new HistoryDao().loadInactiveHistory();
        DefaultMutableTreeNode historyRoot = new DefaultMutableTreeNode("History");
        for (DaakiaHistory h : deletedHistory) {
            historyRoot.add(new DefaultMutableTreeNode(h));
        }
        historyTrashTree.setModel(new DefaultTreeModel(historyRoot));
        TreeUtils.expandAllNodes(historyTrashTree);

        DaakiaStore store = new CollectionDao().loadInactiveStore();
        DefaultMutableTreeNode colRoot = new DefaultMutableTreeNode("Collections");
        if(store != null) {
            DaakiaUtils.convertCollectionStoreToTreeNode(store, colRoot);
        }
        collectionTrashTree.setModel(new DefaultTreeModel(colRoot));
        collectionTrashTree.setRootVisible(false);
        TreeUtils.expandAllNodes(collectionTrashTree);
    }
}
