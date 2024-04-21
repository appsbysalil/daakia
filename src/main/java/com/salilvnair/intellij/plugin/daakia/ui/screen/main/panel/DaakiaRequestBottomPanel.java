package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.RequestHeaderPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRequestBottomPanel extends BaseDaakiaPanel<DaakiaRequestBottomPanel> {

    private JBTabbedPane tabbedPane;
    private RequestHeaderPanel requestHeaderPanel;
    private RequestBodyContainer requestBodyContainer;

    public DaakiaRequestBottomPanel(JRootPane rootPane, DataContext dataContext) {
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
        requestHeaderPanel = new RequestHeaderPanel(rootPane, dataContext);
        requestBodyContainer = new RequestBodyContainer(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }


    @Override
    public void initChildrenLayout() {
        tabbedPane.addTab("Request Headers", AllIcons.Actions.Minimap, requestHeaderPanel);
        tabbedPane.addTab("Request Body", AllIcons.Json.Object, requestBodyContainer);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SEND)) {
                tabbedPane.setSelectedIndex(1);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_ADD_HEADER)) {
                tabbedPane.setSelectedIndex(0);
            }
        });
    }
}
