package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer.HistoryTreeCellRenderer;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
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
    private DefaultTreeModel historyTreeModel;

    public HistoryPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
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
    }

    @Override
    public void initChildrenLayout() {
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        initTreeListeners();
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_AFTER_HISTORY_ADDED)) {
                TreeUtils.expandAllNodes(historyTree);
            }
        });
    }

    private void initTreeNode() {
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_HISTORY, dataContext);
        DefaultMutableTreeNode root = dataContext.uiContext().historyRootNode();
        historyTreeModel = new DefaultTreeModel(root);
        historyTree = new Tree(historyTreeModel);
        historyTree.setRootVisible(false);
        TreeUtils.expandAllNodes(historyTree);
        uiContext().setHistoryTree(historyTree);
        uiContext().setHistoryTreeModel(historyTreeModel);
    }

    public void initTreeListeners() {

        historyTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                    if(userObject instanceof DaakiaHistory) {
                        eventPublisher().onDoubleClickHistoryDataNode((DaakiaHistory) userObject);
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single click
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                    if(userObject instanceof DaakiaHistory) {
                        eventPublisher().onClickHistoryDataNode((DaakiaHistory) userObject);
                    }
                }
                else if (e.getClickCount() == 2) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                    if(userObject instanceof DaakiaHistory) {
                        eventPublisher().onDoubleClickHistoryDataNode((DaakiaHistory) userObject);
                    }
                }
            }
        });
    }
}
