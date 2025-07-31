package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.ui.components.JBTabbedPane;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.CollectionStorePanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.HistoryPanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.TrashPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;

import javax.swing.*;
import java.awt.*;

public class DaakiaSideNavPanel extends BaseDaakiaPanel<DaakiaSideNavPanel> {

    private JBTabbedPane tabbedPane;
    private HistoryPanel historyPanel;
    private CollectionStorePanel collectionStorePanel;
    private TrashPanel trashPanel;

    DaakiaSideNavPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        tabbedPane = new JBTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
        historyPanel = new HistoryPanel(rootPane, dataContext);
        collectionStorePanel = new CollectionStorePanel(rootPane, dataContext);
        trashPanel = new TrashPanel(rootPane, dataContext);
        tabbedPane.addTab("  ", DaakiaIcons.HistoryIcon, historyPanel);
        tabbedPane.addTab("  ", DaakiaIcons.CollectionIcon, collectionStorePanel);
        tabbedPane.addTab("  ", DaakiaIcons.DeleteIcon, trashPanel);
        tabbedPane.setToolTipTextAt(0, "History");
        tabbedPane.setToolTipTextAt(1, "Collection");
        tabbedPane.setToolTipTextAt(2, "Trash");
    }

    @Override
    public void initChildrenLayout() {
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        listenGlobal(event -> {
            if(DaakiaEvent.ofAnyType(event, DaakiaEventType.ON_SELECT_HISTORY_DATA_NODE, DaakiaEventType.ON_DBL_CLICK_HISTORY_DATA_NODE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                sideNavContext().setSelectedDaakiaHistory(daakiaEvent.selectedDaakiaHistory());
                globalEventPublisher().onLoadSelectedHistoryData(dataContext, daakiaEvent.selectedDaakiaHistory());
            }
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_ADD_NEW_COLLECTION)) {
                //persist the user collections
                daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.SAVE_REQUEST_IN_STORE_COLLECTION, dataContext);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_DELETE_COLLECTIONS)) {
                //persist the user collections
                daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.MARK_REQUEST_IN_STORE_COLLECTION_FOR_DELETION, dataContext);
            }
            else if(DaakiaEvent.ofAnyType(event, DaakiaEventType.ON_SELECT_STORE_COLLECTION_NODE, DaakiaEventType.ON_DBL_CLICK_STORE_COLLECTION_NODE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                sideNavContext().setSelectedDaakiaStoreRecord(daakiaEvent.selectedDaakiaStoreRecord());
                globalEventPublisher().onLoadSelectedStoreCollectionData(dataContext, daakiaEvent.selectedDaakiaStoreRecord());
//                daakiaService(DaakiaType.APP).execute(AppDaakiaType.ON_CLICK_STORE_COLLECTION_NODE, dataContext);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RIGHT_CLICK_RENAME_STORE_COLLECTION_NODE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                sideNavContext().setSelectedDaakiaStoreRecord(daakiaEvent.selectedDaakiaStoreRecord());
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.ON_RIGHT_CLICK_RENAME_STORE_COLLECTION_NODE, dataContext);
            }
            else if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RIGHT_CLICK_RENAME_HISTORY_NODE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                sideNavContext().setSelectedDaakiaHistory(daakiaEvent.selectedDaakiaHistory());
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.ON_RIGHT_CLICK_RENAME_HISTORY_NODE, dataContext);
            }
        });
    }

}
