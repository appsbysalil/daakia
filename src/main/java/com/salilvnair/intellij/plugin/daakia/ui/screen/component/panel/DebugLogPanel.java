package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;


import javax.swing.*;
import java.awt.*;

/**
 * Simple panel to display debug logs captured during runtime.
 */
public class DebugLogPanel extends BaseDaakiaPanel<DebugLogPanel> {
    private EditorEx logEditor;


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
        Document doc = EditorFactory.getInstance().createDocument("");
        logEditor = (EditorEx) EditorFactory.getInstance().createViewer(doc);
        logEditor.getSettings().setLineNumbersShown(true);
        uiContext().setDebugLogEditor(logEditor);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(logEditor.getComponent(), BorderLayout.CENTER);
    }
}
