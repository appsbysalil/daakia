package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightVerticalSplitPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitPanel> {

    private DaakiaRightVerticalSplitLeftPanel leftPanel;
    private DaakiaRightVerticalSplitRightPanel rightPanel;
    private JSplitPane leftRightSplitPane;


    public DaakiaRightVerticalSplitPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }


    @Override
    public void initComponents() {
        leftPanel = new DaakiaRightVerticalSplitLeftPanel(rootPane, dataContext);
        rightPanel = new DaakiaRightVerticalSplitRightPanel(rootPane, dataContext);
        leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        leftRightSplitPane.setDividerSize(5);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(leftRightSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }
}
