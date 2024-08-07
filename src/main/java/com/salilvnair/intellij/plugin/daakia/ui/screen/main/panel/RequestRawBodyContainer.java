package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.RequestBodyPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class RequestRawBodyContainer extends BaseDaakiaPanel<RequestRawBodyContainer> {
    private JPanel requestBodyActionContainer;
    private RequestBodyPanel requestBodyPanel;
    private IconButton formatJsonBtn;

    public RequestRawBodyContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        requestBodyActionContainer = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        formatJsonBtn = new IconButton(AllIcons.Diff.MagicResolve, new Dimension(30, 30));
        formatJsonBtn.setToolTipText("Format JSON");
        requestBodyActionContainer.add(formatJsonBtn);
        requestBodyPanel = new RequestBodyPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
        add(requestBodyActionContainer, BorderLayout.NORTH);
        add(requestBodyPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        formatJsonBtn.addActionListener(e -> {
            eventPublisher().onClickRequestBodyFormatter();
        });
    }
}
