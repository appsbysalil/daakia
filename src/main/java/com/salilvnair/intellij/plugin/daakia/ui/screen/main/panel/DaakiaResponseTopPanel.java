package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.layout.CustomFlowLayout;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import java.awt.*;

public class DaakiaResponseTopPanel extends BaseDaakiaPanel<DaakiaResponseTopPanel> {

    private JPanel responseMetaDataPanel;
    private JLabel statusLabel;
    private JLabel sizeLabel;
    private JLabel timeLabel;

    public DaakiaResponseTopPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
       setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        responseMetaDataPanel = new JPanel();
        responseMetaDataPanel.setLayout(new CustomFlowLayout(FlowLayout.LEFT, 30, 20));
        statusLabel = new JLabel("Status:");
        sizeLabel = new JLabel("Size:");
        timeLabel = new JLabel("Time:");
        uiContext().setStatusLabel(statusLabel);
        uiContext().setSizeLabel(sizeLabel);
        uiContext().setTimeLabel(timeLabel);
    }

    @Override
    public void initStyle() {
        super.initStyle();
    }

    @Override
    public void initChildrenLayout() {
        responseMetaDataPanel.add(statusLabel);
        responseMetaDataPanel.add(timeLabel);
        responseMetaDataPanel.add(sizeLabel);
        add(responseMetaDataPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event ->{
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RECEIVING_RESPONSE)) {
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_RESPONSE_STATUS, dataContext);
            }
        });
    }
}
