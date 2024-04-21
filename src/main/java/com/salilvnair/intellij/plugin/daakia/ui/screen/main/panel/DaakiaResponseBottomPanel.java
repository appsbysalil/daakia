package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.ResponseHeaderPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaResponseBottomPanel extends BaseDaakiaPanel<DaakiaResponseBottomPanel> {

    private JBTabbedPane tabbedPane;
    private ResponseHeaderPanel responseHeaderPanel;
    private ResponseBodyContainer responseBodyContainer;

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
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }


    @Override
    public void initChildrenLayout() {
        tabbedPane.addTab("Response Body", AllIcons.Json.Object, responseBodyContainer);
        tabbedPane.addTab("Response Headers", AllIcons.Actions.Minimap, responseHeaderPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SEND)) {
                tabbedPane.setSelectedIndex(0);
            }
        });
    }
}
