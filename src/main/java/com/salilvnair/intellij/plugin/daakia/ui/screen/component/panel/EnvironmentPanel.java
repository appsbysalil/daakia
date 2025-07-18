package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.salilvnair.intellij.plugin.daakia.persistence.EnvironmentDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.PasswordInputField;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.GlobalContext;
import com.intellij.icons.AllIcons;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class EnvironmentPanel extends BaseDaakiaPanel<EnvironmentPanel> {
    private JComboBox<Environment> environmentCombo;
    private JTextField nameField;
    private JPanel variablePanel;
    private JPanel variableScrollPanel;
    private JBScrollPane variableScrollPane;
    private Map<String, VariableRow> variableRows;
    private JButton addButton;
    private JButton newButton;
    private JButton deleteButton;
    private JButton saveButton;
    private final GlobalContext globalContext;

    public EnvironmentPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        this.globalContext = dataContext.globalContext();
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout(5,5));
    }

    @Override
    public void initComponents() {
        environmentCombo = new ComboBox<>(globalContext.environments().toArray(new Environment[0]));
        if(environmentCombo.getItemCount() == 0) {
            Environment env = new Environment();
            env.setName("New Environment");
            globalContext.environments().add(env);
            environmentCombo.addItem(env);
            environmentCombo.setSelectedItem(env);
            globalContext.setSelectedEnvironment(env);
        }
        nameField = new JTextField(15);
        variablePanel = new JPanel();
        variablePanel.setLayout(new BoxLayout(variablePanel, BoxLayout.Y_AXIS));
        variableScrollPanel = new JPanel();
        variableScrollPanel.add(variablePanel);
        variableScrollPane = new JBScrollPane(variableScrollPanel);
        variableRows = new LinkedHashMap<>();
        addButton = new JButton("Add");
        addButton.setIcon(AllIcons.Actions.AddList);
        newButton = new JButton("Create New", AllIcons.General.Add);
        deleteButton = new JButton("Delete", DaakiaIcons.DeleteIcon);
        saveButton = new JButton("Save");
    }

    @Override
    public void initChildrenLayout() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftHeaderPanel.add(new JLabel("Environment:"));
        leftHeaderPanel.add(environmentCombo);
        leftHeaderPanel.add(new JLabel("Name:"));
        leftHeaderPanel.add(nameField);
        leftHeaderPanel.add(saveButton);
        leftHeaderPanel.add(newButton);
        leftHeaderPanel.add(deleteButton);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        rightHeaderPanel.add(addButton);

        headerPanel.add(leftHeaderPanel, BorderLayout.CENTER);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        variableScrollPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(variableScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        environmentCombo.addActionListener(e -> loadSelected());
        addButton.addActionListener(e -> addVariableRow(null, "default", null, null));
        newButton.addActionListener(e -> createNewEnvironment());
        deleteButton.addActionListener(e -> deleteSelectedEnvironment());
        saveButton.addActionListener(e -> saveCurrentEnvironment());
        loadSelected();
    }

    private void createNewEnvironment() {
        Environment env = new Environment();
        env.setName("New Environment");
        globalContext.environments().add(env);
        environmentCombo.addItem(env);
        environmentCombo.setSelectedItem(env);
        globalContext.setSelectedEnvironment(env);
        nameField.setText(env.getName());
        variablePanel.removeAll();
        variableRows.clear();
        variablePanel.revalidate();
        variablePanel.repaint();
        globalEventPublisher().onEnvironmentListChanged();
    }

    private void deleteSelectedEnvironment() {
        Environment env = (Environment) environmentCombo.getSelectedItem();
        if(env != null) {
            globalContext.environments().remove(env);
            environmentCombo.removeItem(env);
            nameField.setText("");
            variablePanel.removeAll();
            variableRows.clear();
            variablePanel.revalidate();
            variablePanel.repaint();
            new EnvironmentDao().saveEnvironments(globalContext.environments());
            globalEventPublisher().onEnvironmentListChanged();
        }
    }

    private void saveCurrentEnvironment() {
        Environment env = (Environment) environmentCombo.getSelectedItem();
        if(env == null) {
            env = new Environment();
            globalContext.environments().add(env);
            environmentCombo.addItem(env);
        }
        env.setName(nameField.getText());
        env.getVariables().clear();
        for(VariableRow row : variableRows.values()) {
            String key = row.keyField.getText();
            String type = (String) row.typeCombo.getSelectedItem();
            String initialVal = getPasswordText(row.initialField);
            String currentVal = getPasswordText(row.currentField);
            if(key != null && !key.isEmpty()) {
                Variable v = new Variable();
                v.setKey(key);
                v.setType(type);
                v.setInitialValue(initialVal);
                v.setCurrentValue(currentVal);
                env.getVariables().add(v);
            }
        }
        new EnvironmentDao().saveEnvironments(globalContext.environments());
        environmentCombo.repaint();
        globalEventPublisher().onEnvironmentListChanged();
        closeTab();
    }

    private void loadSelected() {
        Environment env = dataContext.globalContext().selectedEnvironment();
        env = env == null ? (Environment) environmentCombo.getSelectedItem() : env;
        if(env==null && environmentCombo.getItemCount() > 0) {
            env = environmentCombo.getItemAt(0);
        }
        else {
            environmentCombo.setSelectedItem(env);
        }
        if(env != null) {
            nameField.setText(env.getName());
            variablePanel.removeAll();
            variableRows.clear();
            for(Variable v: env.getVariables()) {
                addVariableRow(v.getKey(), v.getType(), v.getInitialValue(), v.getCurrentValue());
            }
        }
        else {
            nameField.setText("");
            variablePanel.removeAll();
            variableRows.clear();
        }
        variablePanel.revalidate();
        variablePanel.repaint();
    }

    private void addVariableRow(String key, String type, String initialVal, String currentVal) {
        String rowId = UUID.randomUUID().toString();
        TextInputField keyField = new TextInputField("Key");
        keyField.setPreferredSize(new Dimension(300, 25));
        keyField.setMaximumSize(new Dimension(300, 25));
        if(key != null) keyField.setText(key);

        ComboBox<String> typeCombo = new ComboBox<>(new String[]{"default", "secret"});
        typeCombo.setSelectedItem(type != null ? type : "default");
        typeCombo.setPreferredSize(new Dimension(150, 25));
        typeCombo.setMaximumSize(new Dimension(150, 25));
      
        PasswordInputField initialField = new PasswordInputField("Initial");
        if(initialVal != null) initialField.setText(initialVal);
        initialField.setPreferredSize(new Dimension(400, 25));
        initialField.setMaximumSize(new Dimension(400, 25));
        PasswordInputField currentField = new PasswordInputField("Current");
        if(currentVal != null) currentField.setText(currentVal);
        currentField.setPreferredSize(new Dimension(400, 25));
        currentField.setMaximumSize(new Dimension(400, 25));

        IconButton deleteButton = new IconButton(DaakiaIcons.DeleteIcon, new Dimension(30,25));

        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rowPanel.add(keyField);
        rowPanel.add(typeCombo);
        rowPanel.add(initialField);
        rowPanel.add(currentField);
        rowPanel.add(deleteButton);

        variablePanel.add(rowPanel);
        variablePanel.revalidate();
        variablePanel.repaint();

        VariableRow varRow = new VariableRow(rowId, keyField, typeCombo, initialField, currentField, deleteButton, rowPanel);
        variableRows.put(rowId, varRow);

        updateSecretFields(varRow);

        initialField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                updateSecretFields(varRow);
            }
        });
        currentField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                updateSecretFields(varRow);
            }
        });

        typeCombo.addActionListener(e -> updateSecretFields(varRow));
        deleteButton.addActionListener(e -> {
            variablePanel.remove(rowPanel);
            variableRows.remove(rowId);
            variablePanel.revalidate();
            variablePanel.repaint();
        });
    }

    private void updateSecretFields(VariableRow row) {
        boolean secret = "secret".equals(row.typeCombo.getSelectedItem());
        row.initialField.setEchoChar(secret && row.initialField.containsText() ? '•' : (char)0);
        row.currentField.setEchoChar(secret && row.currentField.containsText() ? '•' : (char)0);
    }

    private String getPasswordText(PasswordInputField field) {
        if(!field.containsText()) {
            return "";
        }
        return new String(field.getPassword());
    }

    private void closeTab() {
        JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
        if(tabbedPane != null) {
            int index = tabbedPane.indexOfComponent(this);
            if(index != -1) {
                tabbedPane.removeTabAt(index);
            }
        }
    }

    private static class VariableRow {
        final String id;
        final TextInputField keyField;
        final ComboBox<String> typeCombo;
        final PasswordInputField initialField;
        final PasswordInputField currentField;
        final IconButton deleteButton;
        final JPanel rowPanel;

        VariableRow(String id, TextInputField keyField, ComboBox<String> typeCombo,
                    PasswordInputField initialField, PasswordInputField currentField,
                    IconButton deleteButton, JPanel rowPanel) {
            this.id = id;
            this.keyField = keyField;
            this.typeCombo = typeCombo;
            this.initialField = initialField;
            this.currentField = currentField;
            this.deleteButton = deleteButton;
            this.rowPanel = rowPanel;
        }
    }
}
