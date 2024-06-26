package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.ResponseBodyPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import javax.swing.*;
import java.awt.*;

public class ResponseBodyContainer extends BaseDaakiaPanel<ResponseBodyContainer> {
    private ResponseBodyPanel responseBodyPanel;

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
        responseBodyPanel = new ResponseBodyPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(responseBodyPanel, BorderLayout.CENTER);
    }
}
