package com.salilvnair.intellij.plugin.daakia.ui.screen.main.frame;

import com.salilvnair.intellij.plugin.daakia.ui.core.awt.SwingComponent;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.DaakiaMainPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

;

public class DaakiaMainFrame extends JFrame implements SwingComponent {
    private DaakiaMainPanel daakiaMainPanel;
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
        DataContext dataContext = new DataContext(this);
        daakiaMainPanel = new DaakiaMainPanel(getRootPane(), dataContext);
    }

    @Override
    public void initChildrenLayout() {
        add(daakiaMainPanel, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {

    }
}
