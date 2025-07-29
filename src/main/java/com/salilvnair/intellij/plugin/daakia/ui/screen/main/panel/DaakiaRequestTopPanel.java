package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRequestTopPanel extends BaseDaakiaPanel<DaakiaRequestTopPanel> {
    private DaakiaRequestTopBarPanel topBarPanel;
    private DaakiaRequestAddHeaderButtonPanel daakiaRequestAddHeaderButtonPanel;
    private JPanel topBarHeaderButtonContainer;


    public DaakiaRequestTopPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        topBarHeaderButtonContainer = new JPanel();
        topBarHeaderButtonContainer.setLayout(new BoxLayout(topBarHeaderButtonContainer, BoxLayout.Y_AXIS));
        topBarPanel = new DaakiaRequestTopBarPanel(rootPane, dataContext);
        daakiaRequestAddHeaderButtonPanel = new DaakiaRequestAddHeaderButtonPanel(rootPane, dataContext);
    }

    @Override
    public void initChildrenLayout() {
        topBarHeaderButtonContainer.add(topBarPanel);
        topBarHeaderButtonContainer.add(daakiaRequestAddHeaderButtonPanel);
        add(topBarHeaderButtonContainer, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }

}
