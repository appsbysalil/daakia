package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.ResponseBodyPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class ResponseBodyContainer extends BaseDaakiaPanel<ResponseBodyContainer> {
    private JPanel responseBodyActionContainer;
    private ResponseBodyPanel responseBodyPanel;
    private IconButton formatJsonBtn;

    public ResponseBodyContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        responseBodyActionContainer = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        formatJsonBtn = new IconButton(AllIcons.Diff.MagicResolve, new Dimension(30, 30));
        formatJsonBtn.setToolTipText("Format JSON");
        responseBodyActionContainer.add(formatJsonBtn);
        responseBodyPanel = new ResponseBodyPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
//        add(responseBodyActionContainer, BorderLayout.NORTH);
        add(responseBodyPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        formatJsonBtn.addActionListener(e -> {
            eventPublisher().onClickResponseBodyFormatter();
        });
    }
}
