package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightVerticalSplitPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitPanel> {

    private DaakiaRightVerticalSplitLeftPanel leftPanel;
    private DaakiaRightVerticalSplitRightPanel rightPanel;
    private JSplitPane leftRightSplitPane;
    private JPanel rightPanelContainer;
    private JProgressBar progressBar;


    public DaakiaRightVerticalSplitPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }


    @Override
    public void initComponents() {
        leftPanel = new DaakiaRightVerticalSplitLeftPanel(rootPane, dataContext);
        rightPanel = new DaakiaRightVerticalSplitRightPanel(rootPane, dataContext);
        rightPanelContainer = new JPanel();
        rightPanelContainer.setLayout(new BoxLayout(rightPanelContainer, BoxLayout.Y_AXIS));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        rightPanelContainer.add(progressBar);
        rightPanelContainer.add(rightPanel);
        leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanelContainer);
        leftRightSplitPane.setDividerSize(2);
        uiContext().setProgressBar(progressBar);
    }

    @Override
    public void initStyle() {
        leftRightSplitPane.setUI(DaakiaUtils.thinDivider());
        leftRightSplitPane.setBorder(null);
    }

    @Override
    public void initChildrenLayout() {
        add(leftRightSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        listen(event -> {
            if (DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SEND)) {
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    rightPanel.setVisible(false);
                    progressBar.setVisible(true);
                });
            }
            else if (DaakiaEvent.ofType(event, DaakiaEventType.AFTER_REST_EXCHANGE)) {
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater(() -> {
                    rightPanel.setVisible(true);
                    progressBar.setVisible(false);
                });
            }
        });
    }
}
