package com.salilvnair.intellij.plugin.daakia.ui.core.awt;

/**
 * @author Salil V Nair
 */
public interface SwingComponent {

    default void init() {
        initLayout();
        initComponents();
        initStyle();
        initListeners();
        initChildrenLayout();
    }

    default void initLayout() {}

    default void initStyle() {}

    default void initComponents() {}

    default void initChildrenLayout() {}

    default void initListeners() {}


}
