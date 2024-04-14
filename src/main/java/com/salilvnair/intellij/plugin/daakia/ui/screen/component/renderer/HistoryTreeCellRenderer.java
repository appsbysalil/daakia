package com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer;

import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class HistoryTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            if (userObject instanceof DaakiaHistory) {
                label.setText(((DaakiaHistory) userObject).render());
            }
        }
        setIcon(null);
        return label;
    }
}
