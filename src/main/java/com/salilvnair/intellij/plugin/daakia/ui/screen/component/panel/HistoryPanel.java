package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer.HistoryTreeCellRenderer;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TextFieldUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HistoryPanel extends BaseDaakiaPanel<HistoryPanel> {
    private JScrollPane scrollPane;
    private Tree historyTree;
    private JPanel searchPanel;
    TextInputField searchTextField;
    private boolean loaded = false;

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
        historyTree = new Tree(new DefaultTreeModel(new DefaultMutableTreeNode("History")));
        historyTree.setRootVisible(false);
        historyTree.setOpaque(false);
        historyTree.setBackground(UIUtil.getTreeBackground());
        historyTree.setCellRenderer(new HistoryTreeCellRenderer());
        historyTree.setToggleClickCount(0);
        scrollPane = new JBScrollPane(historyTree);
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
                loadData();
            }
        });

        TextFieldUtils.addChangeListener(searchTextField, e -> {
            TextInputField textInputField = (TextInputField) e.getSource();
            if(textInputField.containsText()) {
                loadData();
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.SEARCH_HISTORY, dataContext, searchTextField.getText());
            }
        });
    }

    public void loadData() {
        if (loaded) return;
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_HISTORY, dataContext);
        DefaultMutableTreeNode root = dataContext.sideNavContext().historyRootNode();
        DefaultTreeModel model = new DefaultTreeModel(root);
        historyTree.setModel(model);
        sideNavContext().setHistoryTree(historyTree);
        sideNavContext().setHistoryTreeModel(model);
        loaded = true;
    }

    private void setTreeBusy(boolean busy) {
        if (historyTree != null) {
            historyTree.setPaintBusy(busy);
        }
    }

    public void initTreeListeners() {

        historyTree.addTreeSelectionListener(e -> {
            loadData();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) historyTree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getUserObject() instanceof DaakiaHistory) {
                Object userObject = selectedNode.getUserObject();
                globalEventPublisher().onSelectHistoryDataNode((DaakiaHistory) userObject);
            }
        });

        historyTree.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            @Override
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent event) {
                loadData();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                Object obj = node.getUserObject();
                if (node.getLevel() == 1 && obj instanceof String year) {
                    if (node.getChildCount() == 1 && isPlaceholder(node.getFirstChild())) {
                        DefaultMutableTreeNode placeholder = (DefaultMutableTreeNode) node.getFirstChild();
                        placeholder.setUserObject("Loading...");
                        ((DefaultTreeModel) historyTree.getModel()).nodeChanged(placeholder);
                        setTreeBusy(true);
                        new HistoryDao().loadMonthsAsync(year, months -> {
                            node.removeAllChildren();
                            if (months != null && !months.isEmpty()) {
                                for (String m : months) {
                                    DefaultMutableTreeNode mn = new DefaultMutableTreeNode(new MonthItem(m));
                                    mn.add(new DefaultMutableTreeNode("Loading"));
                                    node.add(mn);
                                }
                            }
                            DefaultTreeModel model = (DefaultTreeModel) historyTree.getModel();
                            model.reload(node);
                            SwingUtilities.invokeLater(() -> historyTree.expandPath(new TreePath(node.getPath())));
                            setTreeBusy(false);
                        });
                    }
                } else if (node.getLevel() == 2 && obj instanceof MonthItem monthItem) {
                    String year = ((DefaultMutableTreeNode) node.getParent()).getUserObject().toString();
                    if (node.getChildCount() == 1 && isPlaceholder(node.getFirstChild())) {
                        DefaultMutableTreeNode placeholder = (DefaultMutableTreeNode) node.getFirstChild();
                        placeholder.setUserObject("Loading...");
                        ((DefaultTreeModel) historyTree.getModel()).nodeChanged(placeholder);
                        setTreeBusy(true);
                        new HistoryDao().loadByMonthAsync(year, monthItem.month, list -> {
                            node.removeAllChildren();
                            if (list != null) {
                                for (DaakiaHistory h : list) {
                                    node.add(new DefaultMutableTreeNode(h));
                                }
                            }
                            DefaultTreeModel model = (DefaultTreeModel) historyTree.getModel();
                            model.reload(node);
                            SwingUtilities.invokeLater(() -> historyTree.expandPath(new TreePath(node.getPath())));
                            setTreeBusy(false);
                        });
                    }
                }
            }

            @Override
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent event) {
            }
        });

        historyTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                loadData();
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                    if(userObject instanceof DaakiaHistory) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY(), (DaakiaHistory) userObject);
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                loadData();
                int row = historyTree.getRowForLocation(e.getX(), e.getY());
                if (row != -1 && SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 1) {
                        if (historyTree.isExpanded(row)) {
                            historyTree.collapseRow(row);
                        } else {
                            historyTree.expandRow(row);
                        }
                    } else if (e.getClickCount() == 2) {
                        if (historyTree.isCollapsed(row)) {
                            historyTree.expandRow(row);
                        }
                        Object userObject = TreeUtils.extractSelectedNodeUserObject(historyTree, e);
                        if(userObject instanceof DaakiaHistory) {
                            globalEventPublisher().onDoubleClickHistoryDataNode((DaakiaHistory) userObject);
                        }
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

    private boolean isPlaceholder(TreeNode node) {
        if (node instanceof DefaultMutableTreeNode treeNode) {
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof String text) {
                return text.startsWith("Loading");
            }
        }
        return false;
    }

    private static class MonthItem {
        final String month;
        MonthItem(String month) { this.month = month; }
        @Override
        public String toString() {
            try {
                int m = Integer.parseInt(month);
                return new java.text.DateFormatSymbols().getShortMonths()[m - 1];
            } catch (Exception e) {
                return month;
            }
        }
    }
}
