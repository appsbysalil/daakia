package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.frame.DaakiaMainFrame;
import com.salilvnair.intellij.plugin.daakia.ui.service.core.DaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.factory.DaakiaFactory;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

import java.util.EventObject;

public class DataContext {
    private final DaakiaMainFrame daakiaMainFrame;
    private DaakiaContext daakiaContext;
    private UIContext uiContext;
    private Publisher<EventObject> publisher;
    private DaakiaEventPublisher eventPublisher;


    public DataContext(DaakiaMainFrame daakiaMainFrame) {
        this.daakiaMainFrame = daakiaMainFrame;
        this.daakiaContext = new DaakiaContext();
        this.uiContext = new UIContext();
        this.publisher = new Publisher<>();
        this.eventPublisher = new DaakiaEventPublisher(this.publisher);
    }

    public Publisher<EventObject> publisher() {
        return publisher;
    }

    public DaakiaEventPublisher eventPublisher() {
        return eventPublisher;
    }

    public Publisher<EventObject> subscriber() {
        return publisher;
    }

    public DaakiaContext daakiaContext() {
        return daakiaContext;
    }

    public UIContext uiContext() {
        return uiContext;
    }

    public DaakiaService daakiaService(DaakiaType daakiaType) {
        return DaakiaFactory.generate(daakiaType);
    }

}
