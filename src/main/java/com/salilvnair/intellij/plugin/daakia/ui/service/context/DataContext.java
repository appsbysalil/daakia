package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.intellij.openapi.project.Project;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaGlobalEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.service.core.DaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.factory.DaakiaFactory;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DebugLogManager;
import com.salilvnair.intellij.plugin.daakia.ui.settings.DaakiaSettings;

import java.util.EventObject;

public class DataContext {
    private final GlobalContext globalContext;
    private DaakiaContext daakiaContext;
    private UIContext uiContext;
    private Publisher<EventObject> publisher;
    private DaakiaEventPublisher eventPublisher;
    private final Project project;

    public DataContext(Project project, GlobalContext globalContext) {
        this(project, globalContext, null);
    }

    public DataContext(Project project, GlobalContext globalContext, UIContext uiContext) {
        this.project = project;
        this.globalContext = globalContext;
        this.daakiaContext = new DaakiaContext();
        this.uiContext = uiContext == null ? new UIContext() : uiContext;
        DaakiaSettings settings = DaakiaSettings.getInstance();
        this.uiContext.setDebugMode(settings.getState().debugMode);
        this.uiContext.setScriptLogEnabled(settings.getState().scriptLogEnabled);
        if(this.uiContext.debugMode()) {
            DebugLogManager.startCapture();
        }
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

    public Publisher<EventObject> globalPublisher() {
        return this.globalContext.globalPublisher();
    }

    public DaakiaGlobalEventPublisher globalEventPublisher() {
        return this.globalContext.globalEventPublisher();
    }

    public Publisher<EventObject> globalSubscriber() {
        return this.globalContext.globalSubscriber();
    }

    public DaakiaContext daakiaContext() {
        return daakiaContext;
    }

    public UIContext uiContext() {
        return uiContext;
    }
    public SideNavContext sideNavContext() {
        return globalContext().sideNavContext();
    }

    public DaakiaService daakiaService(DaakiaType daakiaType) {
        return DaakiaFactory.generate(daakiaType);
    }

    public GlobalContext globalContext() {
        return globalContext;
    }

    public Project project() {
        return project;
    }
}
