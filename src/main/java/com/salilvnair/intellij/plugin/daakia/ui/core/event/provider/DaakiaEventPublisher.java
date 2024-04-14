package com.salilvnair.intellij.plugin.daakia.ui.core.event.provider;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
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

    public void onAfterHistoryAdded() {
        DaakiaEvent event = new DaakiaEvent(this, DaakiaEventType.ON_AFTER_HISTORY_ADDED);
        publisher.publish(event);
    }

}
