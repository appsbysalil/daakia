package com.salilvnair.intellij.plugin.daakia.ui.screen.component.renderer;

import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CollectionStoreTreeCellRenderer extends DefaultTreeCellRenderer {
    private JPopupMenu moreOptionsMenu;

    public CollectionStoreTreeCellRenderer() {}

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component renderer = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode node) {
            if (node.getParent() == null) {
                // Root node
                setIcon(DaakiaIcons.CollectionFolder);
            }
            else if (!node.isLeaf()) {
                // Leaf node (file)
                setIcon(DaakiaIcons.CollectionFolder);
            }
            else {
                Object userObject = node.getUserObject();
                if(userObject instanceof DaakiaHistory) {
                    setIcon(DaakiaIcons.HttpRequestsFiletype);
                }
                else {
                    setIcon(DaakiaIcons.CollectionFolder);
                }
            }
        }
        return renderer;
    }


}
