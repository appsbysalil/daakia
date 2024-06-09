package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class RequestAuthorizationPanel extends BaseDaakiaPanel<RequestAuthorizationPanel> {
    private ComboBox<String> authTypes;
    private JPanel authPanel;
    private JPanel basicAuthTextboxPanel;
    private JPanel bearerTokenTextBoxPanel;

    public RequestAuthorizationPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        authTypes = new ComboBox<>(new String[]{"None","Basic Auth", "Bearer Token"});
        authPanel = new JPanel();
        TextInputField userNameTextField = new TextInputField("Username");
        TextInputField bearerTokenTextField = new TextInputField("Token");
        bearerTokenTextField.setPreferredSize(new Dimension(600, 25));

        userNameTextField.setPreferredSize(new Dimension(600, 25));
        TextInputField passwordTextField = new TextInputField("Password");
        passwordTextField.setPreferredSize(new Dimension(600, 25));

        basicAuthTextboxPanel = new JPanel();
        basicAuthTextboxPanel.setLayout(new BorderLayout());
        basicAuthTextboxPanel.setVisible(false);

        bearerTokenTextBoxPanel = new JPanel();
        bearerTokenTextBoxPanel.setLayout(new BorderLayout());
        bearerTokenTextBoxPanel.setVisible(false);

        JPanel userNameContainer = new JPanel();
        userNameContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        userNameContainer.add(userNameTextField);

        JPanel passwordContainer = new JPanel();
        passwordContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        passwordContainer.add(passwordTextField);

        JPanel bearerTokenContainer = new JPanel();
        bearerTokenContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        bearerTokenContainer.add(bearerTokenTextField);

        basicAuthTextboxPanel.add(userNameContainer, BorderLayout.NORTH);
        basicAuthTextboxPanel.add(passwordContainer, BorderLayout.CENTER);

        bearerTokenTextBoxPanel.add(bearerTokenContainer, BorderLayout.NORTH);

        JLabel authTypeLabel = new JLabel("Authentication Type");
        JPanel authTypeHeaderPanel = new JPanel();
        authTypeHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        authTypeHeaderPanel.add(authTypeLabel);
        authTypeHeaderPanel.add(authTypes);


        JPanel txtBoxPanel = new JPanel();
        txtBoxPanel.setLayout(new BoxLayout(txtBoxPanel, BoxLayout.Y_AXIS));
        txtBoxPanel.add(basicAuthTextboxPanel);
        txtBoxPanel.add(bearerTokenTextBoxPanel);

        authPanel.setLayout(new BorderLayout());
        authPanel.add(authTypeHeaderPanel, BorderLayout.NORTH);
        authPanel.add(txtBoxPanel, BorderLayout.CENTER);

        uiContext().setAuthTypes(authTypes);
        uiContext().setUserNameTextField(userNameTextField);
        uiContext().setPasswordTextField(passwordTextField);
        uiContext().setBearerTokenTextField(bearerTokenTextField);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(authPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        authTypes.addActionListener(e -> {
            if ("None".equals(authTypes.getSelectedItem())) {
                basicAuthTextboxPanel.setVisible(false);
                bearerTokenTextBoxPanel.setVisible(false);
            }
            else if ("Basic Auth".equals(authTypes.getSelectedItem())) {
                basicAuthTextboxPanel.setVisible(true);
                bearerTokenTextBoxPanel.setVisible(false);
            }
            else if ("Bearer Token".equals(authTypes.getSelectedItem())) {
                bearerTokenTextBoxPanel.setVisible(true);
                basicAuthTextboxPanel.setVisible(false);
            }
        });
    }
}
