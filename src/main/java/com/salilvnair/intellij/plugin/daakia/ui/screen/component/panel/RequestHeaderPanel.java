package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import java.awt.*;

public class RequestHeaderPanel extends BaseDaakiaPanel<RequestHeaderPanel> {
    private JPanel headerScrollPanel;
    private JPanel headersPanel;
    JBScrollPane headerScrollPane;

    public RequestHeaderPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public void initComponents() {
        headersPanel = new JPanel();
        headerScrollPanel = new JPanel();
        headerScrollPanel.add(headersPanel);
        headerScrollPane = new JBScrollPane(headerScrollPanel);
        uiContext().setHeadersPanel(headersPanel);
        uiContext().setHeaderScrollPanel(headerScrollPanel);
        initDefaultHeaders();
    }

    private void initDefaultHeaders() {
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_REQUEST_HEADER, dataContext, "Content-Type", "application/json");
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        headersPanel.setLayout(new BoxLayout(headersPanel, BoxLayout.Y_AXIS));
        headerScrollPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(headerScrollPanel);
    }

    @Override
    public void initListeners() {
        listen( event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_ADD_HEADER)) {
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_REQUEST_HEADER, dataContext, null, null);
            }
        });
    }
}
