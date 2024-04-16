package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.ExpUiIcons;
import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.CollectionStorePanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.HistoryPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;

import javax.swing.*;
import java.awt.*;

public class DaakiaLeftPanel extends BaseDaakiaPanel<DaakiaLeftPanel> {

    private JBTabbedPane tabbedPane;
    private HistoryPanel historyPanel;
    private CollectionStorePanel collectionStorePanel;

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
    }

    @Override
    public void initComponents() {
        tabbedPane = new JBTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        historyPanel = new HistoryPanel(rootPane, dataContext);
        collectionStorePanel = new CollectionStorePanel(rootPane, dataContext);
        tabbedPane.addTab(null, ExpUiIcons.General.History, historyPanel);
        tabbedPane.addTab(null, DaakiaIcons.CollectionIcon, collectionStorePanel);
        tabbedPane.setToolTipTextAt(0, "History");
        tabbedPane.setToolTipTextAt(1, "Collection");
    }

    @Override
    public void initChildrenLayout() {
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_ADD_NEW_COLLECTION) ||
                    DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_DELETE_COLLECTIONS)) {
                //persist the user collections
                daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.SAVE_REQUEST_IN_STORE_COLLECTION, dataContext);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_STORE_COLLECTION_NODE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                uiContext().setSelectedDaakiaStoreRecord(daakiaEvent.selectedDaakiaStoreRecord());
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.ON_CLICK_STORE_COLLECTION_NODE, dataContext);
            }
        });
    }

}
