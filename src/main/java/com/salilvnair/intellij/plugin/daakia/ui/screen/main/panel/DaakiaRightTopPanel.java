package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightTopPanel extends BaseDaakiaPanel<DaakiaRightTopPanel> {
    private DaakiaTopBarPanel topBarPanel;
    private AddHeaderButtonPanel addHeaderButtonPanel;


    public DaakiaRightTopPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        topBarPanel = new DaakiaTopBarPanel(rootPane, dataContext);
        addHeaderButtonPanel = new AddHeaderButtonPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        debugIfApplicable(addHeaderButtonPanel, "addHeaderButtonPanel");
    }

    @Override
    public void initChildrenLayout() {
        add(topBarPanel, BorderLayout.NORTH);
        add(addHeaderButtonPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }

}
