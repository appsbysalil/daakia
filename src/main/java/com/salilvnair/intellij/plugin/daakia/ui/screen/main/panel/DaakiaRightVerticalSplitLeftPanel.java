package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import javax.swing.*;
import java.awt.*;

public class DaakiaRightVerticalSplitLeftPanel extends BaseDaakiaPanel<DaakiaRightVerticalSplitLeftPanel> {

    private DaakiaRequestTopPanel requestTopPanel;
    private DaakiaRequestBottomPanel requestBottomPanel;



    public DaakiaRightVerticalSplitLeftPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }


    @Override
    public void initComponents() {
        requestTopPanel = new DaakiaRequestTopPanel(rootPane, dataContext);
        requestBottomPanel = new DaakiaRequestBottomPanel(rootPane, dataContext);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        setMinimumSize(new Dimension(700, 0));
    }

    @Override
    public void initChildrenLayout() {
        add(requestTopPanel, BorderLayout.NORTH);
        add(requestBottomPanel, BorderLayout.CENTER);
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
