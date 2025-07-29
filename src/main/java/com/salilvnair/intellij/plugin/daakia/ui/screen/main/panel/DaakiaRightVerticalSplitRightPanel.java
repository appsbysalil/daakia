package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightVerticalSplitRightPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitRightPanel> {

    private DaakiaResponseTopPanel topPanel;
    private DaakiaResponseBottomPanel bottomPanel;

    public DaakiaRightVerticalSplitRightPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }


    @Override
    public void initComponents() {
        topPanel = new DaakiaResponseTopPanel(rootPane, dataContext);
        bottomPanel = new DaakiaResponseBottomPanel(rootPane, dataContext);
    }

    @Override
    public void initChildrenLayout() {
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }
}
