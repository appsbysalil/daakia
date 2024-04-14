package com.salilvnair.intellij.plugin.daakia.ui.core.event.type;

import org.springframework.http.ResponseEntity;

import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class DaakiaEvent extends EventObject {
    private final DaakiaEventType eventType;
    private String deletedHeaderKey;
    private ResponseEntity<String> responseEntity;



    public DaakiaEvent(Object source, DaakiaEventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public String deletedHeaderKey() {
        return deletedHeaderKey;
    }

    public void setDeletedHeaderKey(String deletedHeaderKey) {
        this.deletedHeaderKey = deletedHeaderKey;
    }

    public static boolean ofType(EventObject event, DaakiaEventType eventType) {
        return event instanceof DaakiaEvent && ((DaakiaEvent)event).eventType == eventType;
    }

    public static DaakiaEvent extract(EventObject event) {
        return (DaakiaEvent)event;
    }


    public ResponseEntity<String> responseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<String> responseEntity) {
        this.responseEntity = responseEntity;
    }
}
