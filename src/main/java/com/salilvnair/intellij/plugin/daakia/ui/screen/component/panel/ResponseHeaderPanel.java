package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ResponseHeaderPanel extends BaseDaakiaPanel<ResponseHeaderPanel> {
    JBScrollPane headerScrollPane;
    private JTable table;
    private DefaultTableModel model;

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
        Object[][] data = {
                {"John", "Doe"},
                {"Jane", "Smith"},
                {"Bob", "Johnson"}
        };

        // Column names
        String[] columnNames = {"First Name", "Last Name"};
        model = new DefaultTableModel(data, columnNames);
        table = new JBTable(model);
        table.setTableHeader(null);
        table.setRowHeight(30);
        headerScrollPane = new JBScrollPane(table);
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

    }
}
