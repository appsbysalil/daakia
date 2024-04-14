package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.ExpUiIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.HistoryPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaLeftPanel extends BaseDaakiaPanel<DaakiaLeftPanel> {

    private JBTabbedPane tabbedPane;
    private HistoryPanel historyPanel;

    DaakiaLeftPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        setPreferredSize(new Dimension(250, 0));
    }

    @Override
    public void initComponents() {
        tabbedPane = new JBTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        historyPanel = new HistoryPanel(rootPane, dataContext);
        tabbedPane.addTab(null, ExpUiIcons.General.History, historyPanel);
        tabbedPane.addTab(null, DaakiaIcons.CollectionIcon, new JPanel(new BorderLayout()));
        tabbedPane.setToolTipTextAt(0, "History");
        tabbedPane.setToolTipTextAt(1, "Collection");
    }

    @Override
    public void initChildrenLayout() {
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {

    }

}
