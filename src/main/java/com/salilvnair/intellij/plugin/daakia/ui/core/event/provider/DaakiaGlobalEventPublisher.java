package com.salilvnair.intellij.plugin.daakia.ui.core.event.provider;

import com.intellij.openapi.application.ApplicationManager;
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
        publishSync(DaakiaEventType.ON_CLICK_ADD_HEADER);
    }

    public void onClickDeleteHeaderRow(String headerKey) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_DELETE_HEADER_ROW);
        event.setDeletedHeaderKey(headerKey);
        publishSync(event);
    }

    public void onReceivingResponse(ResponseEntity<String> responseEntity) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RECEIVING_RESPONSE);
        event.setResponseEntity(responseEntity);
        publishSync(event);
    }

    public void onClickSend() {
        publishSync(DaakiaEventType.ON_CLICK_SEND);
    }

    public void onClickSave() {
        publishSync(DaakiaEventType.ON_CLICK_SAVE);
    }

    public void onAfterHistoryAdded() {
        publishSync(DaakiaEventType.ON_AFTER_HISTORY_ADDED);
    }

    public void onSelectHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_SELECT_HISTORY_DATA_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publishSync(event);
    }

    public void onDoubleClickHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_DBL_CLICK_HISTORY_DATA_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publishSync(event);
    }

    public void onLoadSelectedHistoryData(DataContext dataContext, DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_LOAD_SELECTED_HISTORY_DATA);
        event.setDataContext(dataContext);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publishAsync(event); // 🔁 background
    }

    public void onLoadSelectedStoreCollectionData(DataContext dataContext, DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_LOAD_SELECTED_STORE_COLLECTION_DATA);
        event.setDataContext(dataContext);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publishAsync(event); // 🔁 background
    }

    public void onRightClickRenameHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RIGHT_CLICK_RENAME_HISTORY_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publishSync(event);
    }

    public void onSelectStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_SELECT_STORE_COLLECTION_NODE);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publishSync(event);
    }

    public void onDoubleClickStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_DBL_CLICK_STORE_COLLECTION_NODE);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publishSync(event);
    }

    public void onRightClickRenameStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RIGHT_CLICK_RENAME_STORE_COLLECTION_NODE);
        event.setSelectedDaakiaStoreRecord(selectedStoreRecord);
        publishSync(event);
    }

    public void onClickAddNewCollection() {
        publishSync(DaakiaEventType.ON_CLICK_ADD_NEW_COLLECTION);
    }

    public void onClickDeleteCollections() {
        publishSync(DaakiaEventType.ON_CLICK_DELETE_COLLECTIONS);
    }

    public void onRefreshTrashPanel() {
        publishSync(DaakiaEventType.ON_REFRESH_TRASH_PANEL);
    }

    public void onRefreshCollectionStorePanel() {
        publishSync(DaakiaEventType.ON_REFRESH_COLLECTION_STORE_PANEL);
    }

    public void onClickSideNavVisibilityToggler() {
        publishSync(DaakiaEventType.ON_CLICK_SIDE_NAV_VISIBILITY_TOGGLER);
    }

    public void onOpenEnvironmentManager() {
        publishSync(DaakiaEventType.ON_OPEN_ENVIRONMENT_MANAGER);
    }

    public void onEnvironmentListChanged() {
        publishSync(DaakiaEventType.ON_ENVIRONMENT_LIST_CHANGED);
    }

    public void onCurrentEnvironmentChange() {
        publishSync(DaakiaEventType.ON_CURRENT_SELECTED_ENVIRONMENT_CHANGED);
    }

    public void onClickImportPostman() {
        publishSync(DaakiaEventType.ON_CLICK_IMPORT_POSTMAN);
    }

    public void onClickExportPostman() {
        publishSync(DaakiaEventType.ON_CLICK_EXPORT_POSTMAN);
    }

    public void onEnableDebugMode() {
        publishSync(DaakiaEventType.ON_ENABLE_DEBUG_MODE);
    }

    // ------------------------
    // 🔽 Helper methods below
    // ------------------------

    private void publishSync(DaakiaEventType type) {
        publisher.publish(new DaakiaEvent(this, type));
    }

    private void publishSync(DaakiaEvent event) {
        publisher.publish(event);
    }

    private void publishAsync(DaakiaEvent event) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> publisher.publish(event));
    }
}
