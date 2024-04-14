package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class AddHeaderButtonPanel extends BaseDaakiaPanel<AddHeaderButtonPanel> {

    private JButton addHeaderButton;

    public AddHeaderButtonPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
       setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10));
    }

    @Override
    public void initComponents() {
        addHeaderButton = new JButton("Add Header");
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        addHeaderButton.setIcon(AllIcons.Actions.AddList);
    }

    @Override
    public void initChildrenLayout() {
        add(addHeaderButton);
    }

    @Override
    public void initListeners() {
        addHeaderButton.addActionListener(e -> {
//            dataContext.publisher().publish(new DaakiaEvent(this, DaakiaEventType.ON_CLICK_ADD_HEADER));
            eventPublisher().onClickAddHeader();
        });
    }


}
