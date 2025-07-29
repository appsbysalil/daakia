package com.salilvnair.intellij.plugin.daakia.ui.core.awt;

/**
 * @author Salil V Nair
 */
public interface SwingComponent {

    default void init(SwingComponent component) {
        initLayout();
        initComponents();
        debugIfApplicable(component);
        initStyle();
        initListeners();
        initChildrenLayout();
    }

    default void initLayout() {}

    default void initStyle() {}

    default void debugIfApplicable(Object instance) {

    }

    default void initComponents() {}

    default void initChildrenLayout() {}

    default void initListeners() {}


}
