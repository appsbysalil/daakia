package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;


public class DaakiaMainPanel extends BaseDaakiaPanel<DaakiaMainPanel> {
    private JSplitPane leftCenterSplitPane;

    //left panel
    private DaakiaLeftPanelWithHeader daakiaLeftPanelWithHeader;


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
        daakiaLeftPanelWithHeader = new DaakiaLeftPanelWithHeader(rootPane, dataContext);
        centerPanel = new DaakiaRightPanel(rootPane, dataContext);
        leftCenterSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, daakiaLeftPanelWithHeader, centerPanel);
        leftCenterSplitPane.setDividerLocation(300);
        leftCenterSplitPane.setDividerSize(5);
    }

    @Override
    public void initChildrenLayout() {
        add(leftCenterSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SIDE_NAV_VISIBILITY_TOGGLER)) {
                leftCenterSplitPane.setDividerLocation(leftCenterSplitPane.getDividerLocation() == 42 ? 300 : 42);
            }
        });
    }



}
