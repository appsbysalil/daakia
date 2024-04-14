package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightPanel extends BaseDaakiaPanel<DaakiaRightPanel> {

    private DaakiaRightTopPanel topPanel;
    private DaakiaRightCenterPanel centerPanel;



    public DaakiaRightPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }


    @Override
    public void initComponents() {
        topPanel = new DaakiaRightTopPanel(rootPane, dataContext);
        centerPanel = new DaakiaRightCenterPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        setMinimumSize(new Dimension(1100, 0));
    }

    @Override
    public void initChildrenLayout() {
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }
}
