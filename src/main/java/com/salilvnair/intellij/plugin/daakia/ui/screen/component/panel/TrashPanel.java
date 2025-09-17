package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.BasicButton;
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
    private BasicButton restoreButton;
    private BasicButton deletePermanentlyButton;

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
        restoreButton = new BasicButton("Restore", AllIcons.Actions.Rollback);
        deletePermanentlyButton = new BasicButton("Delete Permanently", DaakiaIcons.DeleteIcon);
        restoreButton.setEnabled(false);
        deletePermanentlyButton.setEnabled(false);
        tabbedPane.addTab("History", new JBScrollPane(historyTrashTree));
        tabbedPane.addTab("Collection", collectionStoreTreePanel);
    }

    @Override
    public void initChildrenLayout() {
        add(tabbedPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
        buttonPanel.add(restoreButton);
        buttonPanel.add(deletePermanentlyButton);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void initListeners() {
        listenGlobal(event -> {
            if (DaakiaEvent.ofType(event, DaakiaEventType.ON_REFRESH_TRASH_PANEL)) {
                loadTrashData();
            }
        });
        collectionTrashTree.addTreeSelectionListener(e -> updateActionButtonsState());
        tabbedPane.addChangeListener(e -> updateActionButtonsState());
        restoreButton.addActionListener(e -> restoreSelectedNode());
        deletePermanentlyButton.addActionListener(e -> deleteSelectedNode());
    }

    public void loadData() {
        loadTrashData();
    }

    private void loadTrashData() {
        com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<DaakiaHistory> deletedHistory = new HistoryDao().loadInactiveHistory();
            DefaultMutableTreeNode historyRoot = new DefaultMutableTreeNode("History");
            for (DaakiaHistory h : deletedHistory) {
                historyRoot.add(new DefaultMutableTreeNode(h));
            }
            com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                historyTrashTree.setModel(new DefaultTreeModel(historyRoot));
                TreeUtils.expandAllNodes(historyTrashTree);
            });

            new CollectionDao().loadStoreAsync(dataContext, false, defaultMutableTreeNode -> {
                DefaultMutableTreeNode inactiveRoot = filterInactiveNodes(defaultMutableTreeNode);
                if (inactiveRoot == null) {
                    inactiveRoot = new DefaultMutableTreeNode("Collections");
                }
                dynamicTree(inactiveRoot, collectionStoreTreePanel);
            });
        });
    }

    public void dynamicTree(DefaultMutableTreeNode defaultMutableTreeNode, JPanel collectionStoreTreePanel) {
        collectionTrashTree.setModel(new DefaultTreeModel(defaultMutableTreeNode));
        collectionTrashTree.setCellRenderer(new CollectionStoreTreeCellRenderer());
        collectionTrashTree.setOpaque(false);
        collectionTrashTree.setBackground(UIUtil.getTreeBackground());
        JScrollPane scrollPane = new JBScrollPane(collectionTrashTree);

        collectionStoreTreePanel.removeAll();
        collectionStoreTreePanel.add(scrollPane, BorderLayout.CENTER);
        // Hide the root node
        collectionTrashTree.setRootVisible(false);

        // Expand all nodes in the collectionStoreTree
        TreeUtils.expandAllNodes(collectionTrashTree);
        DefaultTreeModel collectionTrashTreeModel = (DefaultTreeModel) collectionTrashTree.getModel();
        collectionTrashTreeModel.reload();
        updateActionButtonsState();
    }

    private DefaultMutableTreeNode filterInactiveNodes(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        boolean inactive = userObject instanceof DaakiaBaseStoreData base && !base.isActive();

        DefaultMutableTreeNode filteredNode = new DefaultMutableTreeNode(userObject);
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode filteredChild = filterInactiveNodes(child);
            if (filteredChild != null) {
                filteredNode.add(filteredChild);
            }
        }

        if (inactive || filteredNode.getChildCount() > 0) {
            return filteredNode;
        }
        return null;
    }

    private void updateActionButtonsState() {
        boolean collectionTab = tabbedPane.getSelectedComponent() == collectionStoreTreePanel;
        boolean hasSelection = collectionTrashTree.getLastSelectedPathComponent() != null;
        restoreButton.setEnabled(collectionTab && hasSelection);
        deletePermanentlyButton.setEnabled(collectionTab && hasSelection);
    }

    private void restoreSelectedNode() {
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) collectionTrashTree.getLastSelectedPathComponent();
        if (selected != null && selected.getUserObject() instanceof DaakiaBaseStoreData base) {
            new CollectionDao().markNodeActiveAsync(base.getUuid(), () -> {
                globalEventPublisher().onRefreshTrashPanel();
                globalEventPublisher().onRefreshCollectionStorePanel();
            });
        }
    }

    private void deleteSelectedNode() {
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) collectionTrashTree.getLastSelectedPathComponent();
        if (selected != null && selected.getUserObject() instanceof DaakiaBaseStoreData base) {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure? This action cannot be undone.",
                    "Delete Permanently", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                new CollectionDao().deleteNodeAsync(base.getUuid(), () -> {
                    globalEventPublisher().onRefreshTrashPanel();
                    globalEventPublisher().onRefreshCollectionStorePanel();
                });
            }
        }
    }
}
