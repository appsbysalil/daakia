package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
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
    private DaakiaEditorX logEditor;

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
        logEditor = new DaakiaEditorX(dataContext.project(), true);
        uiContext().setDebugLogEditor(logEditor);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(logEditor, BorderLayout.CENTER);
    }
}
