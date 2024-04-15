package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RestDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TextFieldUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.UrlUtils;

import javax.swing.*;
import java.awt.*;

public class DaakiaTopBarPanel extends BaseDaakiaPanel<DaakiaTopBarPanel> {

    private ComboBox<String> requestTypes;
    private JTextField urlTextField;
    private JButton saveButton;
    private JButton sendButton;

    public DaakiaTopBarPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
    }

    @Override
    public void initComponents() {
        requestTypes = new ComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        urlTextField = new JTextField();
        dataContext.uiContext().setRequestTypes(requestTypes);
        dataContext.uiContext().setUrlTextField(urlTextField);
        sendButton = new JButton("Send");
        saveButton = new JButton("Save");

    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        sendButton.setIcon(AllIcons.Actions.Execute);
        saveButton.setIcon(DaakiaIcons.SaveIcon);
        sendButton.setEnabled(false);
    }

    @Override
    public void initChildrenLayout() {
        add(requestTypes);
        add(urlTextField);
        add(sendButton);
        add(saveButton);
        urlTextField.setPreferredSize(new Dimension(600, 25));
    }

    @Override
    public void initListeners() {

        TextFieldUtils.addChangeListener(urlTextField, e -> {
            sendButton.setEnabled(!urlTextField.getText().isEmpty() && UrlUtils.validateURL(urlTextField.getText()));
        });

        sendButton.addActionListener(e -> {
            eventPublisher().onClickSend();
            DaakiaContext daakiaContext = daakiaService(DaakiaType.REST).execute(RestDaakiaType.EXCHANGE, dataContext);
            eventPublisher().onReceivingResponse(daakiaContext.responseEntity());
            daakiaService(DaakiaType.APP).execute(AppDaakiaType.ADD_HISTORY, dataContext);
            eventPublisher().onAfterHistoryAdded();
            daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.SAVE_HISTORY, dataContext);
        });
    }
}
