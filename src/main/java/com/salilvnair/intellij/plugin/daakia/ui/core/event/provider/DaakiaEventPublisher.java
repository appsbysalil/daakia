package com.salilvnair.intellij.plugin.daakia.ui.core.event.provider;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import org.springframework.http.ResponseEntity;

import java.util.EventObject;

public class DaakiaEventPublisher {
    private final Publisher<EventObject> publisher;

    public DaakiaEventPublisher(Publisher<EventObject> publisher) {
        this.publisher = publisher;
    }

    public void onClickAddHeader() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_ADD_HEADER);
        publisher.publish(event);
    }

    public void onClickFormData() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_FORM_DATA_ADD);
        publisher.publish(event);
    }

    public void onClickDeleteHeaderRow(String headerKey) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_DELETE_HEADER_ROW);
        event.setDeletedHeaderKey(headerKey);
        publisher.publish(event);
    }

    public void onClickDeleteFormDataKeyValueRow(String key) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_DELETE_FORM_DATA_ROW);
        event.setDeletedFormDataKey(key);
        publisher.publish(event);
    }

    public void afterRestApiExchange(DaakiaContext daakiaContext) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.AFTER_REST_EXCHANGE);
        event.setDaakiaContext(daakiaContext);
        publisher.publish(event);
    }

    public void onReceivingResponse(DaakiaContext daakiaContext, ResponseEntity<?> responseEntity) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RECEIVING_RESPONSE);
        event.setResponseEntity(responseEntity);
        event.setDaakiaContext(daakiaContext);
        publisher.publish(event);
    }

    public void onClickSend() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_SEND);
        publisher.publish(event);
    }

    public void onClickStop() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_STOP);
        publisher.publish(event);
    }

    public void onSelectRawBodyType() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_SELECT_RAW_BODY_TYPE);
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

    public void onClickHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_SELECT_HISTORY_DATA_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }

    public void onDoubleClickHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_DBL_CLICK_HISTORY_DATA_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }

    public void onRightClickRenameHistoryDataNode(DaakiaHistory selectedDaakiaHistory) {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_RIGHT_CLICK_RENAME_HISTORY_NODE);
        event.setSelectedDaakiaHistory(selectedDaakiaHistory);
        publisher.publish(event);
    }

    public void onClickStoreCollectionNode(DaakiaStoreRecord selectedStoreRecord) {
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

    public void onRefreshTrashPanel() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_REFRESH_TRASH_PANEL);
        publisher.publish(event);
    }

    public void onRefreshCollectionStorePanel() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_REFRESH_COLLECTION_STORE_PANEL);
        publisher.publish(event);
    }

    public void onClickSideNavVisibilityToggler() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_SIDE_NAV_VISIBILITY_TOGGLER);
        publisher.publish(event);
    }

    public void onClickRequestPanelVisibilityToggler() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_REQUEST_PANEL_VISIBILITY_TOGGLER);
        publisher.publish(event);
    }

    public void onClickResponsePanelVisibilityToggler() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_RESPONSE_PANEL_VISIBILITY_TOGGLER);
        publisher.publish(event);
    }

    public void onClickRequestBodyFormatter() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_REQUEST_BODY_FORMATTER_BTN);
        publisher.publish(event);
    }

    public void onClickResponseBodyFormatter() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_CLICK_RESPONSE_BODY_FORMATTER_BTN);
        publisher.publish(event);
    }

}
