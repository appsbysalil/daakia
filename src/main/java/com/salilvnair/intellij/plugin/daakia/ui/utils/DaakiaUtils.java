package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaStoreRecord;

import javax.swing.tree.DefaultMutableTreeNode;
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
}
