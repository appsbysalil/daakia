package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import javax.swing.*;
import java.awt.*;


public class DaakiaMainPanel extends BaseDaakiaPanel<DaakiaMainPanel> {
    private JSplitPane leftRightSplitPane;

    private JPanel headerPanel;
    private ComboBox<Environment> environmentCombo;
    private IconButton environmentButton;
    private JButton importButton;
    private JButton exportButton;

    //left panel
    private DaakiaSideNavPanelContainer leftSideNavPanelContainer;


    //center panel
    private DaakiaTabbedMainPanel tabbedMainPanel;


    public DaakiaMainPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
        setVisible(true);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        headerPanel = new JPanel(new BorderLayout());
        importButton = new JButton("Import");
        importButton.setIcon(AllIcons.ToolbarDecorator.Import);
        exportButton = new JButton("Export");
        exportButton.setIcon(AllIcons.General.Export);

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftHeaderPanel.add(importButton);
        leftHeaderPanel.add(exportButton);

        environmentButton = new IconButton(AllIcons.Actions.Properties, new Dimension(40,40));
        environmentButton.setToolTipText("Environment");
        environmentCombo = new com.intellij.openapi.ui.ComboBox<>(dataContext.globalContext().environments().toArray(new Environment[0]));
        if(dataContext.globalContext().selectedEnvironment() != null) {
            environmentCombo.setSelectedItem(dataContext.globalContext().selectedEnvironment());
        }
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeaderPanel.add(environmentButton);
        rightHeaderPanel.add(environmentCombo);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        leftSideNavPanelContainer = new DaakiaSideNavPanelContainer(rootPane, dataContext);
        tabbedMainPanel = new DaakiaTabbedMainPanel(rootPane, dataContext);
        leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSideNavPanelContainer, tabbedMainPanel);
        leftRightSplitPane.setDividerLocation(300);
        leftRightSplitPane.setDividerSize(3);
        dataContext.uiContext().setEnvironmentCombo(environmentCombo);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        setSize(1200, 600);
        leftRightSplitPane.setUI(DaakiaUtils.thinDivider());
        leftRightSplitPane.setBorder(null);
    }

    @Override
    public void initChildrenLayout() {
        add(headerPanel, BorderLayout.NORTH);
        add(leftRightSplitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        globalSubscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_SIDE_NAV_VISIBILITY_TOGGLER)) {
                leftRightSplitPane.setDividerLocation(leftRightSplitPane.getDividerLocation() == 42 ? 300 : 42);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_ENVIRONMENT_LIST_CHANGED)) {
                refreshEnvironmentCombo();
            }
        });
        environmentCombo.addActionListener(e -> {
            Environment env = (Environment) environmentCombo.getSelectedItem();
            dataContext.globalContext().setSelectedEnvironment(env);
        });
        importButton.addActionListener(e -> globalEventPublisher().onClickImportPostman());
        exportButton.addActionListener(e -> globalEventPublisher().onClickExportPostman());
        environmentButton.addActionListener(e -> {
            globalEventPublisher().onOpenEnvironmentManager();
        });
    }

    private void refreshEnvironmentCombo() {
        Environment selected = dataContext.globalContext().selectedEnvironment();
        environmentCombo.removeAllItems();
        for(Environment env : dataContext.globalContext().environments()) {
            environmentCombo.addItem(env);
        }
        if(selected != null) {
            environmentCombo.setSelectedItem(selected);
        }
    }


}
