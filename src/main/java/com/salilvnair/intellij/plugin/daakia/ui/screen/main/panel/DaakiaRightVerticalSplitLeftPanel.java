package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightVerticalSplitLeftPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitLeftPanel> {

    private DaakiaRequestTopPanel requestTopPanel;
    private DaakiaRequestBottomPanel requestBottomPanel;
    private JPanel headerPanel;
    private IconButton hideShowButton;
    private JPanel bodyPanel;



    public DaakiaRightVerticalSplitLeftPanel(JRootPane rootPane, DataContext dataContext) {
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
        requestTopPanel = new DaakiaRequestTopPanel(rootPane, dataContext);
        requestBottomPanel = new DaakiaRequestBottomPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        setMinimumSize(new Dimension(700, 0));
    }

    @Override
    public void initChildrenLayout() {
        headerPanel.add(hideShowButton);
        bodyPanel.add(requestTopPanel, BorderLayout.NORTH);
        bodyPanel.add(requestBottomPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        hideShowButton.addActionListener(e -> {
            bodyPanel.setVisible(!bodyPanel.isVisible());
            revalidate();
            repaint();
            globalEventPublisher().onClickRequestPanelVisibilityToggler();
        });
    }
}
