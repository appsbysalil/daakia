package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.BasicButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer.CollectionStoreTreeCellRenderer;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TextFieldUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.PostmanUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class CollectionStorePanel extends BaseDaakiaPanel<CollectionStorePanel> {
    private JScrollPane scrollPane;
    private JPanel collectionStoreTreePanel;
    private Tree collectionStoreTree;
    private DefaultTreeModel collectionStoreTreeModel;
    private JPanel searchPanel;
    TextInputField searchTextField;

    public CollectionStorePanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
       setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        collectionStoreTreePanel = dynamicTree(this);
        scrollPane = new JBScrollPane(collectionStoreTreePanel);
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
        TextFieldUtils.addChangeListener(searchTextField, e -> {
            TextInputField textInputField = (TextInputField) e.getSource();
            if(textInputField.containsText()) {
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.SEARCH_COLLECTION, dataContext, searchTextField.getText());
                TreeUtils.expandAllNodes(collectionStoreTree);
            }
        });
        listenGlobal(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_IMPORT_POSTMAN)) {
                importPostmanCollection1();
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_EXPORT_POSTMAN)) {
                exportPostmanCollection();
            }
        });
    }

    public JPanel dynamicTree(Component parentComponent) {
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_STORE_COLLECTIONS, dataContext);
        DefaultMutableTreeNode rootNode = sideNavContext().collectionStoreRootNode();
        collectionStoreTreeModel = new DefaultTreeModel(rootNode);
        collectionStoreTree = new Tree(collectionStoreTreeModel);
        collectionStoreTree.setCellRenderer(new CollectionStoreTreeCellRenderer());
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane(collectionStoreTree);
        JPanel buttonPanel = new JPanel(new BorderLayout());

        BasicButton addButton = new BasicButton("Add");
        addButton.setIcon(AllIcons.Modules.AddExcludedRoot);

        JPopupMenu moreOptionsMenu = new JPopupMenu();

        // Create menu items with icons
        JMenuItem deleteMenuItem = new JMenuItem(DaakiaIcons.DeleteIcon);
        deleteMenuItem.setEnabled(false);
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
            globalEventPublisher().onClickDeleteCollections();

        });


        collectionStoreTree.addTreeSelectionListener( e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
//            DefaultMutableTreeNode root = (DefaultMutableTreeNode) collectionStoreTree.getModel().getRoot();
            deleteMenuItem.setEnabled(selectedNode != null && selectedNode != rootNode);
        });

        IconButton moreIconButton = new IconButton(AllIcons.Actions.More, new Dimension(40,0));
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
            globalEventPublisher().onClickAddNewCollection();

        });

        moreIconButton.addActionListener(actionEvent -> {
            moreOptionsMenu.show(moreIconButton, 0, moreIconButton.getHeight());
        });

        sideNavContext().setCollectionStoreTree(collectionStoreTree);
        sideNavContext().setCollectionStoreTreeModel(collectionStoreTreeModel);

        return panel;
    }



    public void initTreeListeners() {

        collectionStoreTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getUserObject() instanceof DaakiaStoreRecord) {
                Object userObject = selectedNode.getUserObject();
                globalEventPublisher().onSelectStoreCollectionNode((DaakiaStoreRecord) userObject);
            }
        });

        collectionStoreTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(collectionStoreTree, e);
                    if(userObject instanceof DaakiaStoreRecord) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY(), (DaakiaStoreRecord) userObject);
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(TreeUtils.extractSelectedNodeUserObject(collectionStoreTree, e) == null) {
                    Object root = collectionStoreTree.getModel().getRoot();
                    collectionStoreTree.setSelectionPath(new TreePath(root));
                }
                if (e.getClickCount() == 2) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(collectionStoreTree, e);
                    if(userObject instanceof DaakiaStoreRecord) {
                        globalEventPublisher().onDoubleClickStoreCollectionNode((DaakiaStoreRecord) userObject);
                    }
                }
            }
        });
    }

    private void showPopupMenu(Component component, int x, int y, DaakiaStoreRecord daakiaStoreRecord) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.addActionListener(e -> renameSelectedTreeItem(daakiaStoreRecord));
        popupMenu.add(renameMenuItem);
        popupMenu.show(component, x, y);
    }

    private void renameSelectedTreeItem(DaakiaStoreRecord daakiaStoreRecord) {
        globalEventPublisher().onRightClickRenameStoreCollectionNode(daakiaStoreRecord);
    }

    private void importPostmanCollectionToRootNode(DefaultMutableTreeNode node, DaakiaStore store) {
        try {
            DaakiaUtils.convertCollectionStoreToTreeNode(store, node);
            SwingUtilities.invokeLater(() -> {
                collectionStoreTreeModel.setRoot(node);
                collectionStoreTreeModel.reload();
                TreeUtils.expandAllNodes(collectionStoreTree);
            });
            sideNavContext().setCollectionStoreRootNode(node);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to import: " + ex.getMessage());
        }
    }

    private void importPostmanCollectionToNode(DefaultMutableTreeNode node, DaakiaStore store) {
        try {
            DaakiaUtils.convertCollectionStoreToTreeNode(store, node);
            SwingUtilities.invokeLater(() -> {
                collectionStoreTreeModel.reload();
                TreeUtils.expandAllNodes(collectionStoreTree);
            });
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to import: " + ex.getMessage());
        }
    }

    private void importPostmanCollection1() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                String json = JsonUtils.readJsonFromFile(file);
                DaakiaStore importedStore = PostmanUtils.fromPostmanJson(json);

                if (importedStore.getChildren() == null || importedStore.getChildren().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No valid collections found in file.");
                    return;
                }

                TreePath selectedPath = collectionStoreTree.getSelectionPath();

                if (selectedPath != null) {
                    // Append into selected folder
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                    Object userObject = selectedNode.getUserObject();

                    if (userObject instanceof DaakiaStoreRecord) {
                        JOptionPane.showMessageDialog(this, "Please select a folder to import into.");
                    }
                    else {
                        importPostmanCollectionToNode(selectedNode, importedStore);
                    }
                }
                else {
                    // Append into root
                    importPostmanCollectionToRootNode((DefaultMutableTreeNode) collectionStoreTreeModel.getRoot(), importedStore);
                }

                // Save final tree to DB
                DaakiaStore finalStore = DaakiaUtils.convertTreeToCollectionStore((DefaultMutableTreeNode) collectionStoreTreeModel.getRoot());
                sideNavContext().setDaakiaStore(finalStore);
                new CollectionDao().saveStoreAsync(finalStore);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to import: " + ex.getMessage());
            }
        }
    }


    private void exportPostmanCollection() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) collectionStoreTreeModel.getRoot();
                DaakiaStore store = DaakiaUtils.convertTreeToCollectionStore(root);
                String json = PostmanUtils.toPostmanJson(store);
                File file = chooser.getSelectedFile();
                JsonUtils.writeJsonToFile(json, file);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage());
            }
        }
    }
}
