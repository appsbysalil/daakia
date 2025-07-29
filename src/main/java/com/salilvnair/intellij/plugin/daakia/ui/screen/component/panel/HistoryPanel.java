package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer.HistoryTreeCellRenderer;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TextFieldUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HistoryPanel extends BaseDaakiaPanel<HistoryPanel> {
    private JScrollPane scrollPane;
    private Tree historyTree;
    private JPanel searchPanel;
    TextInputField searchTextField;
    public HistoryPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initStyle() {
        setSize(800, 300);
    }

    @Override
    public void initComponents() {
        initTreeNode();
        scrollPane = new JBScrollPane(historyTree);
        historyTree.setCellRenderer(new HistoryTreeCellRenderer());
        searchPanel = new JPanel(new BorderLayout());
        searchTextField = new TextInputField("Search");
        searchPanel.add(searchTextField, BorderLayout.CENTER);
    }

    @Override
    public void initChildrenLayout() {
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        initTreeListeners();
        listenGlobal(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_AFTER_HISTORY_ADDED)) {
                TreeUtils.expandAllNodes(historyTree);
            }
        });

        TextFieldUtils.addChangeListener(searchTextField, e -> {
            TextInputField textInputField = (TextInputField) e.getSource();
            if(textInputField.containsText()) {
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.SEARCH_HISTORY, dataContext, searchTextField.getText());
                TreeUtils.expandAllNodes(historyTree);
            }
        });
    }

    private void initTreeNode() {
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_HISTORY, dataContext);
        DefaultMutableTreeNode root = dataContext.sideNavContext().historyRootNode();
        DefaultTreeModel historyTreeModel = new DefaultTreeModel(root);
        historyTree = new Tree(historyTreeModel);
        historyTree.setRootVisible(false);
        TreeUtils.expandAllNodes(historyTree);
        sideNavContext().setHistoryTree(historyTree);
        sideNavContext().setHistoryTreeModel(historyTreeModel);
    }

    public void initTreeListeners() {

        historyTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) historyTree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getUserObject() instanceof DaakiaHistory) {
                Object userObject = selectedNode.getUserObject();
                globalEventPublisher().onSelectHistoryDataNode((DaakiaHistory) userObject);
            }
        });

        historyTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                    if(userObject instanceof DaakiaHistory) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY(), (DaakiaHistory) userObject);
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Single click
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                    if(userObject instanceof DaakiaHistory) {
                        globalEventPublisher().onDoubleClickHistoryDataNode((DaakiaHistory) userObject);
                    }
                }
            }
        });
    }


    private void showPopupMenu(Component component, int x, int y, DaakiaHistory daakiaHistory) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.addActionListener(e -> renameSelectedTreeItem(daakiaHistory));
        popupMenu.add(renameMenuItem);
        popupMenu.show(component, x, y);
    }

    private void renameSelectedTreeItem(DaakiaHistory selectedItem) {
        globalEventPublisher().onRightClickRenameHistoryDataNode(selectedItem);
    }
}
