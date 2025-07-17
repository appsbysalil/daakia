package com.salilvnair.intellij.plugin.daakia.ui.screen.component.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import com.salilvnair.intellij.plugin.daakia.persistence.EnvironmentDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.GlobalContext;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EnvironmentManagerDialog extends DialogWrapper {
    private final GlobalContext globalContext;
    private JComboBox<Environment> environmentCombo;
    private JTextField nameField;
    private JTable variableTable;
    private DefaultTableModel model;

    public EnvironmentManagerDialog(GlobalContext globalContext) {
        super(true);
        this.globalContext = globalContext;
        setTitle("Manage Environments");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        environmentCombo = new JComboBox<>(globalContext.environments().toArray(new Environment[0]));
        environmentCombo.addActionListener(e -> loadSelected());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Environment:"));
        top.add(environmentCombo);
        JButton newButton = new JButton("New");
        newButton.addActionListener(e -> {
            Environment env = new Environment();
            env.setName("New Environment");
            globalContext.environments().add(env);
            environmentCombo.addItem(env);
            environmentCombo.setSelectedItem(env);
            nameField.setText(env.getName());
            model.setRowCount(0);
            globalContext.globalEventPublisher().onEnvironmentListChanged();
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            Environment env = (Environment) environmentCombo.getSelectedItem();
            if(env != null) {
                globalContext.environments().remove(env);
                environmentCombo.removeItem(env);
                nameField.setText("");
                model.setRowCount(0);
                new EnvironmentDao().saveEnvironments(globalContext.environments());
                globalContext.globalEventPublisher().onEnvironmentListChanged();
            }
        });
        top.add(newButton);
        top.add(deleteButton);
        nameField = new JTextField(20);
        top.add(new JLabel("Name:"));
        top.add(nameField);
        panel.add(top, BorderLayout.NORTH);
        model = new DefaultTableModel(new Object[]{"Variable","Type","Initial Value","Current Value"},0);
        variableTable = new JBTable(model);
        panel.add(new JScrollPane(variableTable), BorderLayout.CENTER);
        JButton addRow = new JButton("Add");
        addRow.addActionListener(e -> model.addRow(new Object[]{"","","",""}));
        model = new DefaultTableModel(new Object[]{"Key","Value"},0);
        variableTable = new JBTable(model);
        panel.add(new JScrollPane(variableTable), BorderLayout.CENTER);
        loadSelected();
        return panel;
    }

    @Override
    protected void doOKAction() {
        Environment env = (Environment) environmentCombo.getSelectedItem();
        if(env == null) {
            env = new Environment();
            globalContext.environments().add(env);
            environmentCombo.addItem(env);
        }
        env.setName(nameField.getText());
        env.getVariables().clear();
        for(int i=0;i<model.getRowCount();i++) {
            String key = (String) model.getValueAt(i,0);
            String type = (String) model.getValueAt(i,1);
            String initialVal = (String) model.getValueAt(i,2);
            String currentVal = (String) model.getValueAt(i,3);
            if(key!=null && !key.isEmpty()) {
                Variable v = new Variable();
                v.setKey(key);
                v.setType(type);
                v.setInitialValue(initialVal);
                v.setCurrentValue(currentVal);
                env.getVariables().add(v);
            }
        }
        new EnvironmentDao().saveEnvironments(globalContext.environments());
        globalContext.globalEventPublisher().onEnvironmentListChanged();
        super.doOKAction();
    }

    private void loadSelected() {
        Environment env = (Environment) environmentCombo.getSelectedItem();
        if(env==null && environmentCombo.getItemCount()>0) {
            env = environmentCombo.getItemAt(0);
        }
        if(env!=null) {
            nameField.setText(env.getName());
            model.setRowCount(0);
            for(Variable v: env.getVariables()) {
                model.addRow(new Object[]{v.getKey(), v.getType(), v.getInitialValue(), v.getCurrentValue()});

            }
        }
    }
}
