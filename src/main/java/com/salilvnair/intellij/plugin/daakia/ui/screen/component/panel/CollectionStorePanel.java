package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreCollection;
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
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class CollectionStorePanel extends BaseDaakiaPanel<CollectionStorePanel> {
    private JScrollPane scrollPane;
    private Tree collectionStoreTree;
    private DefaultTreeModel collectionStoreTreeModel;
    private JPanel searchPanel;
    private JPanel treeWrapper;
    TextInputField searchTextField;
    private boolean loaded = false;

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
        treeWrapper = new JPanel(new BorderLayout());
        scrollPane = new JBScrollPane(treeWrapper);
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
        TextFieldUtils.addChangeListener(searchTextField, e -> {
            TextInputField textInputField = (TextInputField) e.getSource();
            if(textInputField.containsText()) {
                loadData();
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.SEARCH_COLLECTION, dataContext, searchTextField.getText());
            }
        });
        listenGlobal(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_IMPORT_POSTMAN)) {
                loadData();
                importPostmanCollection();
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_EXPORT_POSTMAN)) {
                loadData();
                exportPostmanCollection();
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_REFRESH_COLLECTION_STORE_PANEL)) {
                if (loaded) {
                    daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_STORE_COLLECTIONS, dataContext);
                }
            }
        });
    }

    public void loadData() {
        if (loaded) return;
        JPanel treePanel = dynamicTree(this);
        treeWrapper.removeAll();
        treeWrapper.add(treePanel, BorderLayout.CENTER);
        treeWrapper.revalidate();
        treeWrapper.repaint();
        initTreeListeners();
        loaded = true;
    }

    private void setTreeBusy(boolean busy) {
        if (collectionStoreTree != null) {
            collectionStoreTree.setPaintBusy(busy);
        }
    }

    public JPanel dynamicTree(Component parentComponent) {
        DefaultMutableTreeNode rootNode = sideNavContext().collectionStoreRootNode();
        collectionStoreTreeModel = new DefaultTreeModel(rootNode);
        collectionStoreTree = new Tree(collectionStoreTreeModel);
        collectionStoreTree.setCellRenderer(new CollectionStoreTreeCellRenderer());
        collectionStoreTree.setOpaque(false);
        collectionStoreTree.setBackground(UIUtil.getTreeBackground());
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
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
            DaakiaBaseStoreData storeData = null;
            if (selectedNode != null) {
                Object userObject = selectedNode.getUserObject();
                if(userObject instanceof DaakiaBaseStoreData baseStoreData) {
                    storeData = baseStoreData;
                }
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                if (parent != null) {
                    collectionStoreTreeModel.removeNodeFromParent(selectedNode);
                    collectionStoreTreeModel.reload(parent);
                }
            }
            globalEventPublisher().onClickDeleteCollections();
            if(storeData != null) {
                String uuid = storeData.getUuid();
                new CollectionDao().markNodeInactiveAsync(uuid, () -> globalEventPublisher().onRefreshTrashPanel());
            }
            else {
                globalEventPublisher().onRefreshTrashPanel();
            }
        });



        collectionStoreTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode latestRootNode = dataContext.sideNavContext().collectionStoreRootNode();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
            deleteMenuItem.setEnabled(selectedNode != null && selectedNode != latestRootNode);
        });

        IconButton moreIconButton = new IconButton(AllIcons.Actions.More, new Dimension(40,0));
        buttonPanel.add(addButton, BorderLayout.WEST);
        buttonPanel.add(moreIconButton, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        // Show the root node so top level collections are visible
        collectionStoreTree.setRootVisible(true);

        addButton.addActionListener(actionEvent -> {
            DefaultMutableTreeNode latestRootNode = dataContext.sideNavContext().collectionStoreRootNode();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
            if(node == null) {
                node = latestRootNode;
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
                DaakiaStoreCollection newCollection = new DaakiaStoreCollection();
                newCollection.setCollectionName(newName);
                newCollection.setCollection(true);
                newCollection.setUuid(DaakiaUtils.generateUUID());
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newCollection);
                newNode.add(new DefaultMutableTreeNode("Loading"));
                node.add(newNode);
                ((DefaultTreeModel) collectionStoreTree.getModel()).reload(node);
                parentComponent.revalidate();
                parentComponent.repaint();
            }
            globalEventPublisher().onClickAddNewCollection();
        });

        moreIconButton.addActionListener(actionEvent -> {
            moreOptionsMenu.show(moreIconButton, 0, moreIconButton.getHeight());
        });

        sideNavContext().setCollectionStoreTree(collectionStoreTree);
        sideNavContext().setCollectionStoreTreeModel(collectionStoreTreeModel);

        daakiaService(DaakiaType.APP).execute(AppDaakiaType.INIT_STORE_COLLECTIONS, dataContext);

        return panel;
    }



    public void initTreeListeners() {

        collectionStoreTree.addTreeSelectionListener(e -> {
            loadData();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getUserObject() instanceof DaakiaStoreRecord) {
                Object userObject = selectedNode.getUserObject();
                globalEventPublisher().onSelectStoreCollectionNode((DaakiaStoreRecord) userObject);
            }
        });

        collectionStoreTree.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            @Override
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent event) {
                loadData();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                Object obj = node.getUserObject();
                if (obj instanceof DaakiaStoreCollection coll) {
                    if (node.getChildCount() == 1 && isPlaceholder(node.getFirstChild())) {
                        DefaultMutableTreeNode placeholder = (DefaultMutableTreeNode) node.getFirstChild();
                        placeholder.setUserObject("Loading...");
                        collectionStoreTreeModel.nodeChanged(placeholder);
                        setTreeBusy(true);
                        new CollectionDao().loadChildrenAsync(coll.getUuid(), children -> {
                            node.removeAllChildren();
                            if (children != null) {
                                for (DefaultMutableTreeNode child : children) {
                                    node.add(child);
                                }
                            }
                            collectionStoreTreeModel.reload(node);
                            SwingUtilities.invokeLater(() -> collectionStoreTree.expandPath(new TreePath(node.getPath())));
                            setTreeBusy(false);
                        });
                    }
                }
            }

            @Override
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent event) {
            }
        });

        collectionStoreTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                loadData();
                if (SwingUtilities.isRightMouseButton(e)) {
                    Object userObject = TreeUtils.extractSelectedNodeUserObject(collectionStoreTree, e);
                    if(userObject instanceof DaakiaStoreRecord) {
                        showPopupMenu(e.getComponent(), e.getX(), e.getY(), (DaakiaStoreRecord) userObject);
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                loadData();
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

    private boolean isPlaceholder(TreeNode node) {
        if (node instanceof DefaultMutableTreeNode treeNode) {
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof String text) {
                return text.startsWith("Loading");
            }
        }
        return false;
    }

    private void importPostmanCollectionToRootNode(DefaultMutableTreeNode node, DaakiaStore store) {
        try {
            if (store.getChildren() != null) {
                for (DaakiaStore child : store.getChildren()) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
                    DaakiaUtils.convertCollectionStoreToTreeNode(child, newNode);
                    node.add(newNode);
                }
            }
            SwingUtilities.invokeLater(() -> {
                collectionStoreTreeModel.reload(node);
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
            if (store.getChildren() != null) {
                for (DaakiaStore child : store.getChildren()) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
                    DaakiaUtils.convertCollectionStoreToTreeNode(child, newNode);
                    node.add(newNode);
                }
            }
            SwingUtilities.invokeLater(() -> {
                collectionStoreTreeModel.reload(node);
                TreeUtils.expandAllNodes(collectionStoreTree);
            });
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to import: " + ex.getMessage());
        }
    }

    private void importPostmanCollection() {
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
                new CollectionDao().saveStoreAsync(dataContext);

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
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) collectionStoreTree.getLastSelectedPathComponent();
                if (selectedNode == null || selectedNode.getUserObject() instanceof DaakiaStoreRecord) {
                    selectedNode = (DefaultMutableTreeNode) collectionStoreTreeModel.getRoot();
                }
                DaakiaStore store = DaakiaUtils.convertTreeToCollectionStore(selectedNode);
                String json = PostmanUtils.toPostmanJson(store);
                File file = chooser.getSelectedFile();
                JsonUtils.writeJsonToFile(json, file);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage());
            }
        }
    }
}
