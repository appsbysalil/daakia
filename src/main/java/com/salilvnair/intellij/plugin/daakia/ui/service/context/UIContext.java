package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.TextInputField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIContext {
    private Map<String, List<TextInputField>> headerTextFields;
    private RSyntaxTextArea requestTextArea;
    private RSyntaxTextArea responseTextArea;
    private ComboBox<String> requestTypes;
    private JTextField urlTextField;

    //history panel related components
    private Tree historyTree;
    private DefaultTreeModel historyTreeModel;
    private Map<String, List<DaakiaHistory>> historyData;
    private DaakiaStore daakiaStore;
    private DefaultMutableTreeNode historyRootNode;
    private DaakiaHistory daakiaHistory;
    private DaakiaHistory selectedDaakiaHistory;

    //headers panel related components
    private JPanel headerScrollPanel;
    private JPanel headersPanel;

    //Daakia store related components
    private Tree collectionStoreTree;
    private DefaultTreeModel collectionStoreTreeModel;
    private DefaultMutableTreeNode collectionStoreRootNode;
    private DaakiaStoreRecord selectedDaakiaStoreRecord;


    public Map<String, List<TextInputField>> headerTextFields() {
        if (headerTextFields == null) {
            headerTextFields =  new HashMap<>();
        }
        return headerTextFields;
    }

    public RSyntaxTextArea requestTextArea() {
        return requestTextArea;
    }

    public void setRequestTextArea(RSyntaxTextArea requestTextArea) {
        this.requestTextArea = requestTextArea;
    }

    public RSyntaxTextArea responseTextArea() {
        return responseTextArea;
    }

    public void setResponseTextArea(RSyntaxTextArea responseTextArea) {
        this.responseTextArea = responseTextArea;
    }

    public ComboBox<String> requestTypes() {
        return requestTypes;
    }

    public void setRequestTypes(ComboBox<String> requestTypes) {
        this.requestTypes = requestTypes;
    }

    public JTextField urlTextField() {
        return urlTextField;
    }

    public void setUrlTextField(JTextField urlTextField) {
        this.urlTextField = urlTextField;
    }

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

    public JPanel headerScrollPanel() {
        return headerScrollPanel;
    }

    public void setHeaderScrollPanel(JPanel headerScrollPanel) {
        this.headerScrollPanel = headerScrollPanel;
    }

    public JPanel headersPanel() {
        return headersPanel;
    }

    public void setHeadersPanel(JPanel headersPanel) {
        this.headersPanel = headersPanel;
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

    public DaakiaStore daakiaStore() {
        return daakiaStore;
    }

    public void setDaakiaStore(DaakiaStore daakiaStore) {
        this.daakiaStore = daakiaStore;
    }

    public DaakiaStoreRecord selectedDaakiaStoreRecord() {
        return selectedDaakiaStoreRecord;
    }

    public void setSelectedDaakiaStoreRecord(DaakiaStoreRecord selectedDaakiaStoreRecord) {
        this.selectedDaakiaStoreRecord = selectedDaakiaStoreRecord;
    }
}
