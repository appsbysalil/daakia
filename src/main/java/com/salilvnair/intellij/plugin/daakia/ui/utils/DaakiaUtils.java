package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreCollection;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.type.DaakiaJavaScriptFileType;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RequestType;
import com.salilvnair.intellij.plugin.daakia.ui.settings.DaakiaSettings;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.MultiValueMap;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DaakiaUtils {
    private DaakiaUtils() {}

    public static DaakiaStore convertTreeToCollectionStore(DefaultMutableTreeNode node) {
        return convertTreeToCollectionStore(new DaakiaStore(), node);
    }

    public static DaakiaStore convertTreeToCollectionStore(DaakiaStore parentNode, DefaultMutableTreeNode node) {
        parentNode.setName(node.getUserObject() != null ? node.getUserObject().toString(): "");
        if(node.getUserObject() instanceof DaakiaStoreCollection daakiaStoreCollection) {
            parentNode.setCollection(daakiaStoreCollection);
        }
        if(node.isLeaf()) {
            if(node.getUserObject() instanceof DaakiaStoreRecord daakiaStoreRecord) {
                parentNode.setRecord(daakiaStoreRecord);
            }
            else {
                parentNode.setEmptyCollection(true);
            }
        }

        // Recursively process child nodes
        if (node.getChildCount() > 0) {
            List<DaakiaStore> children = new ArrayList<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                DaakiaStore child = new DaakiaStore();
                convertTreeToCollectionStore(child, childNode);
                children.add(child);
            }
            parentNode.setChildren(children);
        }

        return parentNode;
    }


    public static DefaultMutableTreeNode convertCollectionStoreToTreeNode(DaakiaStore daakiaStore, DefaultMutableTreeNode treeNode) {
        treeNode.setUserObject(daakiaStore.getRecord() == null ? daakiaStore.getCollection() == null ? daakiaStore.getName() : daakiaStore.getCollection() : daakiaStore.getRecord());
        treeNode.removeAllChildren();

        if(daakiaStore.getChildren() != null && !daakiaStore.getChildren().isEmpty()) {
            for (DaakiaStore childDaakiaStore : daakiaStore.getChildren()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
                convertCollectionStoreToTreeNode(childDaakiaStore, childNode);
                treeNode.add(childNode);
            }
        }
        return treeNode;
    }

    public static DefaultMutableTreeNode convertCollectionStoreToTreeNodeFilterBySearchText(DaakiaStore daakiaStore, DefaultMutableTreeNode collectionStoreRootNode, String searchText) {
        if(daakiaStore.getChildren()!=null && !daakiaStore.getChildren().isEmpty()) {
            for (DaakiaStore childDaakiaStore : daakiaStore.getChildren()) {
                if(childDaakiaStore.getRecord() == null || childDaakiaStore.getRecord().getUrl() != null && childDaakiaStore.getRecord().getUrl().contains(searchText) || childDaakiaStore.getRecord().getDisplayName() != null && childDaakiaStore.getRecord().getDisplayName().contains(searchText)
                ) {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childDaakiaStore.getRecord() == null ? childDaakiaStore.getName() : childDaakiaStore.getRecord());
                    convertCollectionStoreToTreeNodeFilterBySearchText(childDaakiaStore, childNode, searchText);
                    if(childNode.getChildCount() > 0 || (childNode.getUserObject() !=null && childNode.getUserObject() instanceof DaakiaStoreRecord)) {
                        collectionStoreRootNode.add(childNode);
                    }
                }
            }
        }
        return collectionStoreRootNode;
    }

    public static void hidePanelWithAnimation(final JPanel panel, boolean visibility) {
        Timer timer = new Timer(20, new ActionListener() {
            private float alpha = 1f;

            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f;
                if (alpha <= 0f) {
                    ((Timer) e.getSource()).stop();
                    panel.setVisible(visibility);
                }
                else {
                    panel.setOpaque(true);
                    panel.setBackground(new JBColor(new Color(panel.getBackground().getRed(), panel.getBackground().getGreen(), panel.getBackground().getBlue(), (int) (alpha * 255)), new Color(panel.getBackground().getRed(), panel.getBackground().getGreen(), panel.getBackground().getBlue(), (int) (alpha * 255))));
                    panel.repaint();
                }
            }
        });
        timer.start();
    }

    /**
     * @deprecated Use the SQLite based persistence layer instead
     */
    @Deprecated
    public static File storeFile() {
        String userHomePath = FileUtils.userHomePath();
        return new File(userHomePath + File.separator + ".salilvnair" + File.separator + "daakia"+ File.separator + "daakia-store.json");
    }

    /**
     * @deprecated Use the SQLite based persistence layer instead
     */
    @Deprecated
    public static File historyFile() {
        String userHomePath = FileUtils.userHomePath();
        return new File(userHomePath + File.separator + ".salilvnair" + File.separator + "daakia"+ File.separator + "daakia-history.json");
    }

    public static void showAboutDaakia(Component component) {
        String message = """
                <html>
                <font size="5"><b>Daakia 2.0.2 (Build DK-2.0.2)</b></font><br><br>
                Website: <a href="www.salilvnair.com">www.salilvnair.com</a><br>
                Support: <a href="mailto:support@salilvnair.com">support@salilvnair.com</a><br>
                Powered by open source software<br>
                License: MIT<br>
                Copyright © 2025
                <br><br><br>
                </html>
                """;
        JCheckBox scriptCheck = new JCheckBox("Debug Mode");
        scriptCheck.setSelected(DaakiaSettings.getInstance().getState().scriptLogEnabled);
        Object[] params = {message, scriptCheck};
        Object[] options = {"Close"};
        JOptionPane.showOptionDialog(component, params, "About Daakia",
                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, DaakiaIcons.DaakiaIcon48,
                options, options[0]);
        DataContext dataContext = null;
        if(component instanceof com.salilvnair.intellij.plugin.daakia.ui.screen.main.frame.DaakiaMainFrame frame) {
            dataContext = frame.dataContext();
        }
        else if(component instanceof com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel<?> panel) {
            dataContext = panel.dataContext();
        }
        if (dataContext != null) {
            dataContext.uiContext().setScriptLogEnabled(scriptCheck.isSelected());
            if (scriptCheck.isSelected()) {
                DebugLogManager.startCapture();
                dataContext.uiContext().setDebugMode(true);
                DaakiaEditorX daakiaEditorX = dataContext.uiContext().debugLogEditor();
                if (daakiaEditorX != null) {
                    daakiaEditorX.setText(DebugLogManager.getLogs());
                }
                dataContext.globalEventPublisher().onEnableDebugMode();
            }
            else {
                DebugLogManager.stopCapture();
                dataContext.uiContext().setDebugMode(false);
            }
        }
    }

    public static @NotNull BasicSplitPaneUI thinDivider() {
        return new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(Border b) {}

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new JBColor(Gray._213, Gray._50)); // Set the color of the divider
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paint(g);
                    }
                };
            }
        };
    }

    public static void changeTabColorAndText(String tabTitle, String requestType, DataContext dataContext) {
        String hexCode  = ColorUtils.hexCodeByRequestType(RequestType.findByType(requestType));
        tabTitle = LabelUtils.trimLabel(tabTitle, 15);
        dataContext.uiContext().setTabTitle(tabTitle);
        JLabel lblTitle = new JLabel(LabelUtils.coloredText(null, requestType, tabTitle, hexCode));
        int selectedIndex = dataContext.uiContext().dynamicDaakiaTabbedPane().getSelectedIndex();
        Component tabComponentAt = dataContext.uiContext().dynamicDaakiaTabbedPane().getTabComponentAt(selectedIndex);
        if(tabComponentAt instanceof JPanel pnlTab) {
            Component component = pnlTab.getComponent(1);
            pnlTab.remove(component);
            pnlTab.add(lblTitle, 1);
            dataContext.uiContext().setLabelTitle(lblTitle);
            pnlTab.revalidate();
            pnlTab.repaint();
        }
    }

    public static String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Filters a tree by search text.
     * Keeps only nodes where the user object matches searchText (case-insensitive),
     * plus their parent chain to preserve hierarchy.
     */
    public static DefaultMutableTreeNode filterTreeBySearchText(DefaultMutableTreeNode originalRoot, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            // No filtering, just clone original
            return deepCopyNode(originalRoot);
        }

        String search = searchText.toLowerCase();

        // Create a new root to store filtered results
        DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode(originalRoot.getUserObject());

        // Recursively filter children
        filterNodeRecursive(originalRoot, newRoot, search);

        return newRoot;
    }

    /**
     * Recursively filters children based on search text and clones matching nodes into newParent.
     */
    private static void filterNodeRecursive(DefaultMutableTreeNode originalNode,
                                            DefaultMutableTreeNode newParent,
                                            String searchText) {

        for (int i = 0; i < originalNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) originalNode.getChildAt(i);

            boolean match = matchesSearch(child, searchText);

            // Create a new node for the child
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(child.getUserObject());

            // Recursively filter its children
            filterNodeRecursive(child, newChild, searchText);

            // Include this node if:
            // 1. It matches the search, or
            // 2. Any of its descendants matched (newChild has children)
            if (match || newChild.getChildCount() > 0) {
                newParent.add(newChild);
            }
        }
    }

    /**
     * Checks if the node's user object text matches the search text
     */
    private static boolean matchesSearch(DefaultMutableTreeNode node, String searchText) {
        Object userObject = node.getUserObject();
        switch (userObject) {
            case null -> {
                return false;
            }

            // Handle your Daakia types
            case DaakiaStoreCollection coll -> {
                return coll.getCollectionName() != null && coll.getCollectionName().toLowerCase().contains(searchText);
            }
            case DaakiaStoreRecord rec -> {
                // Match on display name or URL
                if (rec.getDisplayName() != null && rec.getDisplayName().toLowerCase().contains(searchText))
                    return true;
                return rec.getUrl() != null && rec.getUrl().toLowerCase().contains(searchText);
            }
            default -> {
                return userObject.toString().toLowerCase().contains(searchText);
            }
        }

    }

    /**
     * Optional: Deep copies a tree node (without filtering).
     */
    private static DefaultMutableTreeNode deepCopyNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node.getUserObject());
        for (int i = 0; i < node.getChildCount(); i++) {
            copy.add(deepCopyNode((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return copy;
    }

    public static FileType resolveFileTypeFromHeaders(MultiValueMap<String, String> headers) {
        try {
            if (headers != null) {
                String contentTypeKey= headers.keySet().stream().filter("Content-Type"::equalsIgnoreCase).findFirst().orElse("Content-Type");
                String contentType = headers.getFirst(contentTypeKey);
                if (contentType != null) {
                    contentType = contentType.toLowerCase();
                    if (contentType.contains("json")) {
                        return JsonFileType.INSTANCE;
                    } else if (contentType.contains("xml")) {
                        return XmlFileType.INSTANCE;
                    } else if (contentType.contains("html")) {
                        return HtmlFileType.INSTANCE;
                    } else if (contentType.contains("javascript") || contentType.contains("ecmascript")) {
                        return DaakiaJavaScriptFileType.INSTANCE;
                    }
                }
            }
        } catch (Exception ignore) {}
        return PlainTextFileType.INSTANCE;
    }
}
