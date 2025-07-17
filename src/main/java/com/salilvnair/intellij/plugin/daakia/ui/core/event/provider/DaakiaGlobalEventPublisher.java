package com.salilvnair.intellij.plugin.daakia.ui.core.event.provider;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.springframework.http.ResponseEntity;

import java.util.EventObject;

public class DaakiaGlobalEventPublisher {
    private final Publisher<EventObject> publisher;

    public DaakiaGlobalEventPublisher(Publisher<EventObject> publisher) {
        this.publisher = publisher;
    }

    public void onClickAddHeader() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_ADD_HEADER);
        publisher.publish(event);
    }

    public void onClickDeleteHeaderRow(String headerKey) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_DELETE_HEADER_ROW);
        event.setDeletedHeaderKey(headerKey);
        publisher.publish(event);
    }

    public void onReceivingResponse(ResponseEntity<String> responseEntity) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RECEIVING_RESPONSE);
        event.setResponseEntity(responseEntity);
        publisher.publish(event);
    }

    public void onClickSend() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_SEND);
        publisher.publish(event);
    }

    public void onClickSave() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_SAVE);
        publisher.publish(event);
    }

    public void onAfterHistoryAdded() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_AFTER_HISTORY_ADDED);
        publisher.publish(event);
    }

    public void onSelectHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_SELECT_HISTORY_DATA_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }

    public void onDoubleClickHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_DBL_CLICK_HISTORY_DATA_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }


    public void onLoadSelectedHistoryData(DataContext dataContext, DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA);
        event.setDataContext(dataContext);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }

    public void onLoadSelectedStoreCollectionData(DataContext dataContext, DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_LOAD_SELECTED_STORE_COLLECTION_DATA);
        event.setDataContext(dataContext);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publisher.publish(event);
    }

    public void onRightClickRenameHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RIGHT_CLICK_RENAME_HISTORY_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }

    public void onSelectStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_SELECT_STORE_COLLECTION_NODE);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publisher.publish(event);
    }

    public void onDoubleClickStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_DBL_CLICK_STORE_COLLECTION_NODE);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publisher.publish(event);
    }

    public void onRightClickRenameStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RIGHT_CLICK_RENAME_STORE_COLLECTION_NODE);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publisher.publish(event);
    }

    public void onClickAddNewCollection() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_ADD_NEW_COLLECTION);
        publisher.publish(event);
    }

    public void onClickDeleteCollections() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_DELETE_COLLECTIONS);
        publisher.publish(event);
    }

    public void onClickSideNavVisibilityToggler() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_SIDE_NAV_VISIBILITY_TOGGLER);
        publisher.publish(event);
    }

    public void onOpenEnvironmentManager() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_OPEN_ENVIRONMENT_MANAGER);
        publisher.publish(event);
    }

    public void onEnvironmentListChanged() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_ENVIRONMENT_LIST_CHANGED);
        publisher.publish(event);
    }

    public void onClickImportPostman() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_IMPORT_POSTMAN);
        publisher.publish(event);
    }

    public void onClickExportPostman() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_EXPORT_POSTMAN);
        publisher.publish(event);
    }

}
