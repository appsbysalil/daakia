package com.salilvnair.intellij.plugin.daakia.ui.archive.util;

import com.intellij.ui.treeStructure.Tree;

import java.awt.event.MouseListener;


public class DaakiaCollectionTree extends Tree {

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
    }
}
