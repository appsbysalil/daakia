package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

/**
 * Simple panel to display debug logs captured during runtime.
 */
public class DebugLogPanel extends BaseDaakiaPanel<DebugLogPanel> {
    private JTextArea logArea;
    private JScrollPane scrollPane;

    public DebugLogPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        scrollPane = new JScrollPane(logArea);
        uiContext().setDebugTextArea(logArea);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(scrollPane, BorderLayout.CENTER);
    }
}
