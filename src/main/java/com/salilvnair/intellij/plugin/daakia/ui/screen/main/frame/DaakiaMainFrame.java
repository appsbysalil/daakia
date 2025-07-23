package com.salilvnair.intellij.plugin.daakia.ui.screen.main.frame;

import com.salilvnair.intellij.plugin.daakia.ui.core.awt.SwingComponent;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.DaakiaMainPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.GlobalContext;

import javax.swing.*;
import java.awt.*;

;

public class DaakiaMainFrame extends JFrame implements SwingComponent {
    private DaakiaMainPanel daakiaMainPanel;
    private DataContext dataContext;
    public DaakiaMainFrame() {
        super("Daakia");
        init();
    }


    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initStyle() {
        setSize(1200, 600);
    }

    @Override
    public void initComponents() {
        dataContext = new DataContext(new GlobalContext());
        daakiaMainPanel = new DaakiaMainPanel(getRootPane(), dataContext);
    }

    @Override
    public void initChildrenLayout() {
        add(daakiaMainPanel, BorderLayout.CENTER);
    }

    public DataContext dataContext() {
        return dataContext;
    }
}
