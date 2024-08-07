package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.RequestFormDataPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class RequestFormDataBodyContainer extends BaseDaakiaPanel<RequestFormDataBodyContainer> {
    private RequestFormDataPanel requestFormDataPanel;
    private IconButton addFormDataKeyValue;
    private JPanel addButtonPanel;

    public RequestFormDataBodyContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        requestFormDataPanel = new RequestFormDataPanel(rootPane, dataContext);
        addButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        addFormDataKeyValue = new IconButton(AllIcons.General.Add, new Dimension(30, 30));
        addFormDataKeyValue.setToolTipText("Add Key Value");
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
        addButtonPanel.add(addFormDataKeyValue);
        add(addButtonPanel, BorderLayout.NORTH);
        add(requestFormDataPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        addFormDataKeyValue.addActionListener(e -> {
            eventPublisher().onClickFormData();
        });
    }
}
