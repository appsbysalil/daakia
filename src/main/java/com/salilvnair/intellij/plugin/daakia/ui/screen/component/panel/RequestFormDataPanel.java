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

public class RequestFormDataPanel extends BaseDaakiaPanel<RequestFormDataPanel> {
    private JPanel formDataScrollPanel;
    private JPanel formDataKeyValuesPanel;
    JBScrollPane formDataScrollPane;

    public RequestFormDataPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public void initComponents() {
        formDataKeyValuesPanel = new JPanel();
        formDataScrollPanel = new JPanel();
        formDataScrollPanel.add(formDataKeyValuesPanel);
        formDataScrollPane = new JBScrollPane(formDataScrollPanel);
        uiContext().setFormDataKeyValuesPanel(formDataKeyValuesPanel);
        uiContext().setFormDataScrollPanel(formDataScrollPanel);
        initDefaultHeaders();
    }

    private void initDefaultHeaders() {
        daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_FORM_DATA, dataContext, "file", "");
    }

    @Override
    public void initChildrenLayout() {
        formDataKeyValuesPanel.setLayout(new BoxLayout(formDataKeyValuesPanel, BoxLayout.Y_AXIS));
        formDataScrollPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        add(formDataScrollPanel);
    }

    @Override
    public void initListeners() {
        listen( event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_FORM_DATA_ADD)) {
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_FORM_DATA, dataContext, null, null);
            }
        });
    }
}
