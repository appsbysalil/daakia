package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RequestType;
import com.salilvnair.intellij.plugin.daakia.ui.settings.DaakiaSettings;
import org.jetbrains.annotations.NotNull;

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
        if(parentNode.getUuid() == null) {
            parentNode.setUuid(java.util.UUID.randomUUID().toString());
        }
        parentNode.setActive("Y");
        parentNode.setCollection(true);
        if(node.isLeaf()) {
            if(node.getUserObject() instanceof DaakiaStoreRecord daakiaStoreRecord) {
                daakiaStoreRecord.setActive("Y");
                if(daakiaStoreRecord.getUuid() == null) {
                    daakiaStoreRecord.setUuid(java.util.UUID.randomUUID().toString());
                }
                parentNode.setRecord(daakiaStoreRecord);
                parentNode.setCollection(false);
            } else {
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

    public static void mergeInactiveNodes(DaakiaStore target, DaakiaStore source) {
        if(source.getChildren() == null) {
            return;
        }
        if(target.getChildren() == null) {
            target.setChildren(new ArrayList<>());
        }
        for(DaakiaStore srcChild : source.getChildren()) {
            DaakiaStore matching = findChildByUuid(target, srcChild.getUuid());
            if(matching == null) {
                if("N".equals(srcChild.getActive())) {
                    target.getChildren().add(srcChild);
                    continue;
                }
                matching = srcChild;
                target.getChildren().add(matching);
            }
            mergeInactiveNodes(matching, srcChild);
        }
    }

    private static DaakiaStore findChildByUuid(DaakiaStore parent, String uuid) {
        if(parent.getChildren() == null || uuid == null) {
            return null;
        }
        for(DaakiaStore c : parent.getChildren()) {
            if(uuid.equals(c.getUuid())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Update active status for the node matching the uuid.
     */
    public static void updateActiveStatusByUuid(DaakiaStore store, String uuid, boolean active) {
        if(store == null || uuid == null) return;
        if(uuid.equals(store.getUuid())) {
            store.setActive(active ? "Y" : "N");
        }
        if(store.getRecord()!=null && uuid.equals(store.getRecord().getUuid())) {
            store.getRecord().setActive(active ? "Y" : "N");
        }
        if(store.getChildren()!=null) {
            for(DaakiaStore child : store.getChildren()) {
                updateActiveStatusByUuid(child, uuid, active);
            }
        }
    }


    public static DefaultMutableTreeNode convertCollectionStoreToTreeNode(DaakiaStore daakiaStore, DefaultMutableTreeNode treeNode) {
        treeNode.setUserObject(daakiaStore.getRecord() == null ? daakiaStore : daakiaStore.getRecord());
        treeNode.removeAllChildren();

        if(daakiaStore.getChildren() != null && !daakiaStore.getChildren().isEmpty()) {
            for (DaakiaStore childDaakiaStore : daakiaStore.getChildren()) {
                if(!"N".equals(childDaakiaStore.getActive())) {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
                    convertCollectionStoreToTreeNode(childDaakiaStore, childNode);
                    treeNode.add(childNode);
                }
            }
        }
        return treeNode;
    }

    public static DefaultMutableTreeNode convertCollectionStoreToTreeNodeOnlyInactive(DaakiaStore daakiaStore, DefaultMutableTreeNode treeNode) {
        if("N".equals(daakiaStore.getActive())) {
            treeNode.setUserObject(daakiaStore.getRecord() == null ? daakiaStore : daakiaStore.getRecord());
        }
        treeNode.removeAllChildren();

        if(daakiaStore.getChildren() != null && !daakiaStore.getChildren().isEmpty()) {
            for (DaakiaStore childDaakiaStore : daakiaStore.getChildren()) {
                if("N".equals(childDaakiaStore.getActive())) {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
                    convertCollectionStoreToTreeNodeOnlyInactive(childDaakiaStore, childNode);
                    treeNode.add(childNode);
                } else {
                    // still traverse to check if any children inactive
                    DefaultMutableTreeNode temp = new DefaultMutableTreeNode();
                    convertCollectionStoreToTreeNodeOnlyInactive(childDaakiaStore, temp);
                    if(temp.getChildCount()>0) {
                        treeNode.add(temp);
                    }
                }
            }
        }
        return treeNode;
    }

    public static DefaultMutableTreeNode convertCollectionStoreToTreeNodeFilterBySearchText(DaakiaStore daakiaStore, DefaultMutableTreeNode collectionStoreRootNode, String searchText) {
        if(daakiaStore.getChildren()!=null && !daakiaStore.getChildren().isEmpty()) {
            for (DaakiaStore childDaakiaStore : daakiaStore.getChildren()) {
                if("N".equals(childDaakiaStore.getActive())) {
                    continue;
                }
                if((childDaakiaStore.getRecord() == null)
                    || (childDaakiaStore.getRecord()!=null &&
                        (childDaakiaStore.getRecord().getUrl()!=null && childDaakiaStore.getRecord().getUrl().contains(searchText) || childDaakiaStore.getRecord().getDisplayName()!=null && childDaakiaStore.getRecord().getDisplayName().contains(searchText)))
                ) {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childDaakiaStore.getRecord() == null ? childDaakiaStore : childDaakiaStore.getRecord());
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
                EditorEx editor = daakiaEditorX.editor();
                if (editor != null) {
                    com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(
                            () -> com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction(
                                    () -> editor.getDocument().setText(DebugLogManager.getLogs()))
                    );
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

}
