package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer.CollectionStoreTreeCellRenderer;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollectionStorePanel extends BaseDaakiaPanel<CollectionStorePanel> {
    private JScrollPane scrollPane;
    private JPanel collectionStoreTreePanel;
    private Tree collectionStoreTree;
    private DefaultTreeModel collectionTreeModel;

    public CollectionStorePanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
       setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        collectionStoreTreePanel = dynamicTree(this);
        scrollPane = new JBScrollPane(collectionStoreTreePanel);
    }

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    public void initChildrenLayout() {
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        collectionStoreTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 1) {
                    if(TreeUtils.extractSelectedNodeUserObject(collectionStoreTree, e) == null) {
                        Object root = collectionStoreTree.getModel().getRoot();
                        collectionStoreTree.setSelectionPath(new TreePath(root));
                    }
                }
            }
        });
    }

    public JPanel dynamicTree(Component parentComponent) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
        collectionTreeModel = new DefaultTreeModel(rootNode);
        collectionStoreTree = new Tree(collectionTreeModel);
        collectionStoreTree.setCellRenderer(new CollectionStoreTreeCellRenderer());
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane(collectionStoreTree);
        JPanel buttonPanel = new JPanel(new BorderLayout());

        BasicButton addButton = new BasicButton("Add");
        addButton.setIcon(AllIcons.Modules.AddExcludedRoot);

        JPopupMenu moreOptionsMenu = new JPopupMenu();

        // Create menu items with icons
        JMenuItem deleteMenuItem = new JMenuItem(DaakiaIcons.DeleteIcon);
        moreOptionsMenu.add(deleteMenuItem);

        deleteMenuItem.addActionListener(e -> {
            TreePath[] selectionPaths = collectionStoreTree.getSelectionPaths();
            if(selectionPaths == null) {
                selectionPaths = new TreePath[]{ collectionStoreTree.getSelectionPath() };
            }
            for (TreePath selectionPath : selectionPaths) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
//            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) collectionStoreTree.getModel().getRoot();
                if (selectedNode != null && selectedNode != rootNode) {
                    DefaultTreeModel model = (DefaultTreeModel) collectionStoreTree.getModel();
                    model.removeNodeFromParent(selectedNode);
                }
            }

        });

        collectionStoreTree.addTreeSelectionListener( e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
//            DefaultMutableTreeNode root = (DefaultMutableTreeNode) collectionStoreTree.getModel().getRoot();
            deleteMenuItem.setEnabled(selectedNode != null && selectedNode != rootNode);
        });

        IconButton moreIconButton = new IconButton(AllIcons.Actions.More);
        buttonPanel.add(addButton, BorderLayout.WEST);
        buttonPanel.add(moreIconButton, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        // Hide the root node
        collectionStoreTree.setRootVisible(false);

        // Expand all nodes in the collectionStoreTree
        TreeUtils.expandAllNodes(collectionStoreTree);

        addButton.addActionListener(actionEvent -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
            if(node == null) {
                node = rootNode;
            }
            String newName = (String) JOptionPane
                    .showInputDialog(
                            parentComponent,
                            "Enter a name",
                            "",
                            JOptionPane.QUESTION_MESSAGE,
                            DaakiaIcons.PackageName48,
                            null,
                            null);
            if(newName!=null && !newName.isEmpty()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newName);
                node.add(newNode);
                ((DefaultTreeModel) collectionStoreTree.getModel()).reload(node);
                parentComponent.revalidate();
                parentComponent.repaint();
            }
            TreeUtils.expandAllNodes(collectionStoreTree);

            System.out.println(JsonUtils.convertTreeToJSON(rootNode));

        });

        moreIconButton.addActionListener(actionEvent -> {
            moreOptionsMenu.show(moreIconButton, 0, moreIconButton.getHeight());
        });


        return panel;
    }
}
