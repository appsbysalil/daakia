package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaGlobalEventPublisher;

import java.util.EventObject;

public class GlobalContext {
    private SideNavContext sideNavContext;
    private Publisher<EventObject> publisher;
    private DaakiaGlobalEventPublisher globalEventPublisher;

    public GlobalContext() {
        this.sideNavContext = new SideNavContext();
        this.publisher = new Publisher<>();
        this.globalEventPublisher = new DaakiaGlobalEventPublisher(publisher);
    }

    public Publisher<EventObject> globalPublisher() {
        return publisher;
    }

    public DaakiaGlobalEventPublisher globalEventPublisher() {
        return globalEventPublisher;
    }

    public Publisher<EventObject> globalSubscriber() {
        return publisher;
    }

    public SideNavContext sideNavContext() {
        return sideNavContext;
    }
}
