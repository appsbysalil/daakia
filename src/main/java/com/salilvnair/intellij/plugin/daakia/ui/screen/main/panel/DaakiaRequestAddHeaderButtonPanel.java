package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class DaakiaRequestAddHeaderButtonPanel extends BaseDaakiaPanel<DaakiaRequestAddHeaderButtonPanel> {

    private JButton addHeaderButton;

    public DaakiaRequestAddHeaderButtonPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
       setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 10));
    }

    @Override
    public void initComponents() {
        addHeaderButton = new JButton("Add Header");
    }

    @Override
    public void initStyle() {
        addHeaderButton.setIcon(AllIcons.Actions.AddList);
    }

    @Override
    public void initChildrenLayout() {
        add(addHeaderButton);
    }

    @Override
    public void initListeners() {
        addHeaderButton.addActionListener(e -> {
            eventPublisher().onClickAddHeader();
        });
    }


}
