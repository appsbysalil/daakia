package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.RequestHeaderPanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.RequestResponseCombinedBodyPanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.ResponseHeaderPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightCenterPanel extends BaseDaakiaPanel<DaakiaRightCenterPanel> {

    private JBTabbedPane tabbedPane;
    private RequestHeaderPanel requestHeaderPanel;
    private RequestResponseCombinedBodyPanel combinedBodyPanel;

    public DaakiaRightCenterPanel(JRootPane rootPane, DataContext dataContext) {
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
        combinedBodyPanel = new RequestResponseCombinedBodyPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }


    @Override
    public void initChildrenLayout() {
        tabbedPane.addTab("Request Headers", AllIcons.Actions.Minimap, requestHeaderPanel);
        tabbedPane.addTab("Body", AllIcons.Json.Object, combinedBodyPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SEND)) {
                tabbedPane.setSelectedIndex(1);
                tabbedPane.addTab("Response Headers", AllIcons.Actions.Minimap, new ResponseHeaderPanel(rootPane, dataContext));
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_ADD_HEADER)) {
                tabbedPane.setSelectedIndex(0);
            }
        });
    }
}
