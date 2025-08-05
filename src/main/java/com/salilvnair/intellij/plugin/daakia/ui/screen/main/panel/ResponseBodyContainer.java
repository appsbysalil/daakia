package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.ResponseBodyPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class ResponseBodyContainer extends BaseDaakiaPanel<ResponseBodyContainer> {
    private ResponseBodyPanel responseBodyPanel;
    private JPanel headerPanel;

    public ResponseBodyContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        responseBodyPanel = new ResponseBodyPanel(rootPane, dataContext);
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    }

    @Override
    public void initChildrenLayout() {
        add(headerPanel, BorderLayout.NORTH);
        add(responseBodyPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }
}
