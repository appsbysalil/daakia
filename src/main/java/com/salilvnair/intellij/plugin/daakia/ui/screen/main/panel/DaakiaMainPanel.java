package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;

import javax.swing.*;
import java.awt.*;


public class DaakiaMainPanel extends BaseDaakiaPanel<DaakiaMainPanel> {
    private JSplitPane leftRightSplitPane;

    //left panel
    private DaakiaSideNavPanelContainer leftSideNavPanelContainer;


    //center panel
    private DaakiaTabbedMainPanel tabbedMainPanel;


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
    public void initComponents() {
        leftSideNavPanelContainer = new DaakiaSideNavPanelContainer(rootPane, dataContext);
        tabbedMainPanel = new DaakiaTabbedMainPanel(rootPane, dataContext);
        leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSideNavPanelContainer, tabbedMainPanel);
        leftRightSplitPane.setDividerLocation(300);
        leftRightSplitPane.setDividerSize(3);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        setSize(1200, 600);
        leftRightSplitPane.setUI(DaakiaUtils.thinDivider());
        leftRightSplitPane.setBorder(null);
    }

    @Override
    public void initChildrenLayout() {
        add(leftRightSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        globalSubscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SIDE_NAV_VISIBILITY_TOGGLER)) {
                leftRightSplitPane.setDividerLocation(leftRightSplitPane.getDividerLocation() == 42 ? 300 : 42);
            }
        });
    }



}
