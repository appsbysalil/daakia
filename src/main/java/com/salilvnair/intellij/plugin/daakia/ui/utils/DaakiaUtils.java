package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
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
        parentNode.setName(node.getUserObject().toString());
        parentNode.setCollection(true);
        if(node.isLeaf()) {
            if(node.getUserObject() instanceof DaakiaStoreRecord daakiaStoreRecord) {
                parentNode.setRecord(daakiaStoreRecord);
                parentNode.setCollection(false);
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


    public static DefaultMutableTreeNode convertCollectionStoreToTreeNode(DaakiaStore daakiaStore, DefaultMutableTreeNode rootNode) {
        if(daakiaStore.getChildren()!=null && !daakiaStore.getChildren().isEmpty()) {
            for (DaakiaStore childDaakiaStore : daakiaStore.getChildren()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childDaakiaStore.getRecord() == null ? childDaakiaStore.getName() : childDaakiaStore.getRecord());
                convertCollectionStoreToTreeNode(childDaakiaStore, childNode);
                rootNode.add(childNode);
            }
        }
        return rootNode;
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

    public static File storeFile() {
        String userHomePath = FileUtils.userHomePath();
        return new File(userHomePath + File.separator + ".salilvnair" + File.separator + "daakia"+ File.separator + "daakia-store.json");
    }

    public static File historyFile() {
        String userHomePath = FileUtils.userHomePath();
        return new File(userHomePath + File.separator + ".salilvnair" + File.separator + "daakia"+ File.separator + "daakia-history.json");
    }

    public static void showAboutDaakia(Component component) {
        String message = """
                <html><font size="5"><b>Daakia 1.0.5 (Build DK-1.0.5)</b></font>
               
                <html>Website: <a href="www.salilvnair.com">www.salilvnair.com</a></html>
                <html>Support: <a href="mailto:support@salilvnair.com">support@salilvnair.com</a></html>
                
                Powered by open source software
                License: MIT
                Copyright © 2024
                """;
        JOptionPane.showMessageDialog(component, message, "About Daakia", JOptionPane.ERROR_MESSAGE, DaakiaIcons.DaakiaIcon48);
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

}
