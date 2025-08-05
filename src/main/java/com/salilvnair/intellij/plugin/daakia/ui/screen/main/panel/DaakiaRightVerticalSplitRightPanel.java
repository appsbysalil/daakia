package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightVerticalSplitRightPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitRightPanel> {

    private DaakiaResponseTopPanel topPanel;
    private DaakiaResponseBottomPanel bottomPanel;
    private JPanel headerPanel;
    private IconButton hideShowButton;
    private JPanel bodyPanel;

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
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        hideShowButton = new IconButton(AllIcons.General.LayoutEditorPreview, new Dimension(40,40));
        bodyPanel = new JPanel(new BorderLayout());
        topPanel = new DaakiaResponseTopPanel(rootPane, dataContext);
        bottomPanel = new DaakiaResponseBottomPanel(rootPane, dataContext);
    }

    @Override
    public void initChildrenLayout() {
        headerPanel.add(hideShowButton);
        bodyPanel.add(topPanel, BorderLayout.NORTH);
        bodyPanel.add(bottomPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        hideShowButton.addActionListener(e -> {
            bodyPanel.setVisible(!bodyPanel.isVisible());
            revalidate();
            repaint();
            globalEventPublisher().onClickResponsePanelVisibilityToggler();
        });
    }
}
