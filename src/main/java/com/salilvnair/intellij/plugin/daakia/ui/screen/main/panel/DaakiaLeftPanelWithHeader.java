package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaLeftPanelWithHeader extends BaseDaakiaPanel<DaakiaLeftPanelWithHeader> {

    private DaakiaLeftPanel leftPanel;
    private JPanel headerPanel;
    private IconButton hideShowButton;

    public DaakiaLeftPanelWithHeader(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        leftPanel  = new DaakiaLeftPanel(rootPane, dataContext);
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
            eventPublisher().onClickSideNavVisibilityToggler();
        });
    }
}
