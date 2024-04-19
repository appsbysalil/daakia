package com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer;

import com.intellij.util.ui.JBUI;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class CustomHistoryTreeCellRenderer implements TreeCellRenderer {
    JLabel requestTypeLabel;

    JLabel displayNameLabel;

    JPanel renderer;

    DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    Color backgroundSelectionColor;

    Color backgroundNonSelectionColor;

    public CustomHistoryTreeCellRenderer() {
        renderer = new JPanel(new GridBagLayout());
        requestTypeLabel = new JLabel(" ");
        renderer.add(requestTypeLabel, createGbc(0, 0));
        displayNameLabel = new JLabel(" ");
        renderer.add(displayNameLabel, createGbc(1, 0));
        backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        Component returnValue = null;
        if ((value instanceof DefaultMutableTreeNode)) {
            Object userObject = ((DefaultMutableTreeNode) value)
                    .getUserObject();
            if (userObject instanceof DaakiaHistory daakiaHistory) {
                requestTypeLabel.setText(daakiaHistory.getRequestType());
                displayNameLabel.setText(daakiaHistory.getUrl());
                if (selected) {
                    renderer.setBackground(backgroundSelectionColor);
                }
                else {
                    renderer.setBackground(backgroundNonSelectionColor);
                }
                renderer.setEnabled(tree.isEnabled());
                returnValue = renderer;
            }
        }
        if (returnValue == null) {
            returnValue = defaultRenderer.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }

    private static GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.EAST;
        int gap = 1;
        gbc.insets = JBUI.insets(gap, gap, gap, gap);
        return gbc;
    }
}