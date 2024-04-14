package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public final class TreeUtils {
    private TreeUtils() {}

    public static void expandAllNodes(Tree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root));
    }

    public static void expandAll(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
    }

    public static DefaultMutableTreeNode findValueNode(DefaultMutableTreeNode treeNode, String date) {
        Enumeration<?> enumeration = treeNode.breadthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.getUserObject()!=null && node.getUserObject().equals(date)) {
                return node;
            }
        }
        return null;
    }

}
