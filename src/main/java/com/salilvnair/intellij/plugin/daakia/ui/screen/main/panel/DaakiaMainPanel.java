package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;


public class DaakiaMainPanel extends BaseDaakiaPanel<DaakiaMainPanel> {
    private JSplitPane leftCenterSplitPane;

    //left panel
    private DaakiaLeftPanel leftPanel;


    //center panel
    private DaakiaRightPanel centerPanel;

    public DaakiaMainPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
        setVisible(true);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        setSize(1200, 600);
    }

    @Override
    public void initComponents() {
        leftPanel = new DaakiaLeftPanel(rootPane, dataContext);
        centerPanel = new DaakiaRightPanel(rootPane, dataContext);
        leftCenterSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        leftCenterSplitPane.setDividerLocation(300);
    }

    @Override
    public void initChildrenLayout() {
        add(leftCenterSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }



}
