package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
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
        init(this);
    }

    @Override
    public void initLayout() {
       setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        responseMetaDataPanel = new JPanel();
        responseMetaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 20));
        statusLabel = new JLabel("<html>&nbsp;&nbsp;Status:</html>");
        sizeLabel = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Size:</html>");
        timeLabel = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Time:</html>");
        uiContext().setStatusLabel(statusLabel);
        uiContext().setSizeLabel(sizeLabel);
        uiContext().setTimeLabel(timeLabel);
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
        listen(event ->{
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RECEIVING_RESPONSE)) {
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_RESPONSE_STATUS, dataContext);
            }
        });
    }
}
