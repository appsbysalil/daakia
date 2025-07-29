package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.RequestFormDataPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import javax.swing.*;
import java.awt.*;

public class RequestFormDataBodyContainer extends BaseDaakiaPanel<RequestFormDataBodyContainer> {
    private RequestFormDataPanel requestFormDataPanel;

    public RequestFormDataBodyContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        requestFormDataPanel = new RequestFormDataPanel(rootPane, dataContext);
    }

    @Override
    public void initChildrenLayout() {
        add(requestFormDataPanel, BorderLayout.CENTER);
    }
}
