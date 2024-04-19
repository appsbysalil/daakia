package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SideNavContext {
    //history panel related components
    private Tree historyTree;
    private DefaultTreeModel historyTreeModel;
    private Map<String, List<DaakiaHistory>> historyData;
    private DaakiaStore daakiaStore;
    private DefaultMutableTreeNode historyRootNode;
    private DaakiaHistory daakiaHistory;
    private DaakiaHistory selectedDaakiaHistory;

    //Daakia store related components
    private JPanel collectionStoreTreePanel;
    private Tree collectionStoreTree;
    private DefaultTreeModel collectionStoreTreeModel;
    private DefaultMutableTreeNode collectionStoreRootNode;
    private DaakiaStoreRecord selectedDaakiaStoreRecord;

    public Tree historyTree() {
        return historyTree;
    }

    public void setHistoryTree(Tree historyTree) {
        this.historyTree = historyTree;
    }

    public DefaultTreeModel historyTreeModel() {
        return historyTreeModel;
    }

    public void setHistoryTreeModel(DefaultTreeModel historyTreeModel) {
        this.historyTreeModel = historyTreeModel;
    }

    public Map<String, List<DaakiaHistory>> historyData() {
        if (historyData == null) {
            historyData = new HashMap<>();
        }
        return historyData;
    }

    public void setHistoryData(Map<String, List<DaakiaHistory>> historyData) {
        this.historyData = historyData;
    }

    public DefaultMutableTreeNode historyRootNode() {
        if(historyRootNode == null) {
            historyRootNode = new DefaultMutableTreeNode("History");
        }
        return historyRootNode;
    }

    public void setHistoryRootNode(DefaultMutableTreeNode historyRootNode) {
        this.historyRootNode = historyRootNode;
    }

    public DaakiaHistory daakiaHistory() {
        return daakiaHistory;
    }

    public void setDaakiaHistory(DaakiaHistory daakiaHistory) {
        this.daakiaHistory = daakiaHistory;
    }

    public DaakiaHistory selectedDaakiaHistory() {
        return selectedDaakiaHistory;
    }

    public void setSelectedDaakiaHistory(DaakiaHistory selectedDaakiaHistory) {
        this.selectedDaakiaHistory = selectedDaakiaHistory;
    }

    public DaakiaStore daakiaStore() {
        return daakiaStore;
    }

    public void setDaakiaStore(DaakiaStore daakiaStore) {
        this.daakiaStore = daakiaStore;
    }

    public Tree collectionStoreTree() {
        return collectionStoreTree;
    }

    public void setCollectionStoreTree(Tree collectionStoreTree) {
        this.collectionStoreTree = collectionStoreTree;
    }

    public DefaultTreeModel collectionStoreTreeModel() {
        return collectionStoreTreeModel;
    }

    public void setCollectionStoreTreeModel(DefaultTreeModel collectionStoreTreeModel) {
        this.collectionStoreTreeModel = collectionStoreTreeModel;
    }

    public DefaultMutableTreeNode collectionStoreRootNode() {
        if(collectionStoreRootNode == null) {
            collectionStoreRootNode = new DefaultMutableTreeNode("Collections");
        }
        return collectionStoreRootNode;
    }

    public void setCollectionStoreRootNode(DefaultMutableTreeNode collectionStoreRootNode) {
        this.collectionStoreRootNode = collectionStoreRootNode;
    }

    public DaakiaStoreRecord selectedDaakiaStoreRecord() {
        return selectedDaakiaStoreRecord;
    }

    public void setSelectedDaakiaStoreRecord(DaakiaStoreRecord selectedDaakiaStoreRecord) {
        this.selectedDaakiaStoreRecord = selectedDaakiaStoreRecord;
    }

    public JPanel collectionStoreTreePanel() {
        return collectionStoreTreePanel;
    }

    public void setCollectionStoreTreePanel(JPanel collectionStoreTreePanel) {
        this.collectionStoreTreePanel = collectionStoreTreePanel;
    }
}
