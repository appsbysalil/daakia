package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.ResponseHeaderPanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.DebugLogPanel;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DebugLogManager;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.intellij.openapi.editor.ex.EditorEx;

import javax.swing.*;
import java.awt.*;

public class DaakiaResponseBottomPanel extends BaseDaakiaPanel<DaakiaResponseBottomPanel> {

    private JBTabbedPane tabbedPane;
    private ResponseHeaderPanel responseHeaderPanel;
    private ResponseBodyContainer responseBodyContainer;
    private DebugLogPanel debugLogPanel;
    private boolean debugTabAdded = false;

    public DaakiaResponseBottomPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        tabbedPane = new JBTabbedPane();
        responseHeaderPanel = new ResponseHeaderPanel(rootPane, dataContext);
        responseBodyContainer = new ResponseBodyContainer(rootPane, dataContext);
        debugLogPanel = new DebugLogPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }


    @Override
    public void initChildrenLayout() {
        tabbedPane.addTab("Response Body", AllIcons.Json.Object, responseBodyContainer);
        tabbedPane.addTab("Response Headers", AllIcons.Actions.Minimap, responseHeaderPanel);
        if(uiContext().debugMode()) {
            enableDebugTab();
        }
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        listen(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SEND)) {
                tabbedPane.setSelectedIndex(0);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.AFTER_REST_EXCHANGE)) {
                EditorEx editor = uiContext().debugLogEditor();
                if(editor != null) {
                    editor.getDocument().setText(DebugLogManager.getLogs());
                }
            }
        });
        listenGlobal(e -> {
            if(DaakiaEvent.ofType(e, DaakiaEventType.ON_ENABLE_DEBUG_MODE)) {
                enableDebugTab();
            }
        });
    }

    private void enableDebugTab() {
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
            if(!debugTabAdded) {
                tabbedPane.addTab("Debug Mode", null, debugLogPanel);
                debugTabAdded = true;
            }
            EditorEx editor = uiContext().debugLogEditor();
            if(editor != null) {
                editor.getDocument().setText(DebugLogManager.getLogs());
            }
        });
    }
}
