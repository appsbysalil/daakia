package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.PasswordInputField;
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
        userNameTextField.setPreferredSize(new Dimension(600, 25));

        PasswordInputField passwordTextField = new PasswordInputField("Password");
        passwordTextField.setPreferredSize(new Dimension(600, 25));
        IconButton passwordToggle = new IconButton(DaakiaIcons.EyeIcon, new Dimension(30,25));

        PasswordInputField bearerTokenTextField = new PasswordInputField("Token");
        bearerTokenTextField.setPreferredSize(new Dimension(600,25));
        IconButton bearerToggle = new IconButton(DaakiaIcons.EyeIcon, new Dimension(30,25));

        basicAuthTextboxPanel = new JPanel();
        basicAuthTextboxPanel.setLayout(new BorderLayout());
        basicAuthTextboxPanel.setVisible(false);

        bearerTokenTextBoxPanel = new JPanel();
        bearerTokenTextBoxPanel.setLayout(new BorderLayout());
        bearerTokenTextBoxPanel.setVisible(false);

        JPanel userNameContainer = new JPanel();
        userNameContainer.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        userNameContainer.add(userNameTextField);

        JPanel passwordContainer = new JPanel();
        passwordContainer.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        passwordContainer.add(passwordTextField);
        passwordContainer.add(passwordToggle);

        JPanel bearerTokenContainer = new JPanel();
        bearerTokenContainer.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        bearerTokenContainer.add(bearerTokenTextField);
        bearerTokenContainer.add(bearerToggle);

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

        passwordToggle.addActionListener(e -> togglePasswordField(passwordTextField, passwordToggle));
        bearerToggle.addActionListener(e -> togglePasswordField(bearerTokenTextField, bearerToggle));
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

    private void togglePasswordField(JPasswordField field, IconButton button) {
        boolean visible = field.getEchoChar() == 0;
        if (visible) {
            field.setEchoChar('\u2022');
            button.setIcon(DaakiaIcons.EyeIcon);
        }
        else {
            field.setEchoChar((char) 0);
            button.setIcon(DaakiaIcons.EyeOffIcon);
        }
    }
}
