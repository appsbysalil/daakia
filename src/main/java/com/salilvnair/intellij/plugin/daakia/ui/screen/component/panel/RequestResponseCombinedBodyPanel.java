package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import java.awt.*;

public class RequestResponseCombinedBodyPanel extends BaseDaakiaPanel<RequestResponseCombinedBodyPanel> {

    private RequestBodyPanel requestBodyPanel;
    private ResponseBodyPanel responseBodyPanel;
    private JSplitPane splitPane;

    public RequestResponseCombinedBodyPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        requestBodyPanel = new RequestBodyPanel(rootPane, dataContext);
        responseBodyPanel = new ResponseBodyPanel(rootPane, dataContext);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, requestBodyPanel, responseBodyPanel);
    }

    @Override
    public void initStyle() {
       debugIfApplicable(this);
       splitPane.setResizeWeight(0.5);
       splitPane.setDividerSize(5);
    }

    @Override
    public void initChildrenLayout() {
        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_HISTORY_DATA_NODE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                uiContext().setSelectedDaakiaHistory(daakiaEvent.selectedDaakiaHistory());
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.ON_CLICK_HISTORY_NODE, dataContext);
            }
        });
    }
}
