package com.salilvnair.intellij.plugin.daakia.ui.core.event.type;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.EventObject;

/**
 * @author Salil V Nair
 */
public class DaakiaEvent extends EventObject {
    private final DaakiaEventType eventType;
    private String deletedHeaderKey;
    private String deletedFormDataKey;
    private ResponseEntity<?> responseEntity;
    private DaakiaHistory selectedDaakiaHistory;
    private DaakiaStoreRecord selectedDaakiaStoreRecord;
    private DataContext dataContext;
    private DaakiaContext daakiaContext;


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

    public static boolean ofAnyType(EventObject event, DaakiaEventType... eventTypes) {
        return event instanceof DaakiaEvent && Arrays.stream(eventTypes).anyMatch(e -> e == ((DaakiaEvent) event).eventType);
    }

    public static DaakiaEvent extract(EventObject event) {
        return (DaakiaEvent)event;
    }


    public ResponseEntity<?> responseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity<?> responseEntity) {
        this.responseEntity = responseEntity;
    }

    public DaakiaHistory selectedDaakiaHistory() {
        return selectedDaakiaHistory;
    }

    public void setSelectedDaakiaHistory(DaakiaHistory selectedDaakiaHistory) {
        this.selectedDaakiaHistory = selectedDaakiaHistory;
    }

    public DaakiaStoreRecord selectedDaakiaStoreRecord() {
        return selectedDaakiaStoreRecord;
    }

    public void setSelectedDaakiaStoreRecord(DaakiaStoreRecord selectedDaakiaStoreRecord) {
        this.selectedDaakiaStoreRecord = selectedDaakiaStoreRecord;
    }

    public DataContext dataContext() {
        return dataContext;
    }

    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    public DaakiaContext daakiaContext() {
        return daakiaContext;
    }

    public void setDaakiaContext(DaakiaContext daakiaContext) {
        this.daakiaContext = daakiaContext;
    }

    public String deletedFormDataKey() {
        return deletedFormDataKey;
    }

    public void setDeletedFormDataKey(String deletedFormDataKey) {
        this.deletedFormDataKey = deletedFormDataKey;
    }
}
