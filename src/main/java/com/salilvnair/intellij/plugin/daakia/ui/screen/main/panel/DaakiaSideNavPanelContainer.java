package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaSideNavPanelContainer extends BaseDaakiaPanel<DaakiaSideNavPanelContainer> {

    private DaakiaSideNavPanel leftPanel;
    private JPanel headerPanel;
    private IconButton hideShowButton;

    public DaakiaSideNavPanelContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        leftPanel  = new DaakiaSideNavPanel(rootPane, dataContext);
        hideShowButton = new IconButton(AllIcons.General.LayoutEditorPreview, new Dimension(40,40));
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
        headerPanel.add(hideShowButton);
        add(headerPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        hideShowButton.addActionListener(e -> {
            globalEventPublisher().onClickSideNavVisibilityToggler();
        });
    }
}
