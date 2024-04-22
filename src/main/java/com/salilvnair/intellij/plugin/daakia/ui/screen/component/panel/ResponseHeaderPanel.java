package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ResponseHeaderPanel extends BaseDaakiaPanel<ResponseHeaderPanel> {
    JBScrollPane headerScrollPane;
    private JTable responseHeaderTable;
    private DefaultTableModel responseHeaderTableModel;

    public ResponseHeaderPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        // Column names
        String[] columnNames = {"Header", "Value"};
        responseHeaderTableModel = new DefaultTableModel(null, columnNames);
        responseHeaderTable = new JBTable(responseHeaderTableModel);
//        table.setTableHeader(null);
        responseHeaderTable.setRowHeight(30);
        headerScrollPane = new JBScrollPane(responseHeaderTable);
        uiContext().setResponseHeaderTable(responseHeaderTable);
        uiContext().setResponseHeaderTableModel(responseHeaderTableModel);
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
        add(headerScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event ->{
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RECEIVING_RESPONSE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                if(daakiaEvent.responseEntity() != null) {
                    daakiaService(DaakiaType.APP).execute(AppDaakiaType.CREATE_RESPONSE_HEADERS, dataContext);
                }
            }
        });
    }
}
