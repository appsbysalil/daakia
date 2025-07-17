package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaGlobalEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.persistence.EnvironmentDao;
import com.salilvnair.intellij.plugin.daakia.ui.settings.DaakiaSettings;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class GlobalContext {
    private SideNavContext sideNavContext;
    private Publisher<EventObject> publisher;
    private DaakiaGlobalEventPublisher globalEventPublisher;
    private List<Environment> environments;
    private Environment selectedEnvironment;

    public GlobalContext() {
        this.sideNavContext = new SideNavContext();
        this.publisher = new Publisher<>();
        this.globalEventPublisher = new DaakiaGlobalEventPublisher(publisher);
        this.environments = new EnvironmentDao().loadEnvironments();
        int envId = DaakiaSettings.getInstance().getState().lastEnvironmentId;
        if(envId > 0 && envId <= environments.size()) {
            this.selectedEnvironment = environments.get(envId - 1);
        } else if(!environments.isEmpty()) {
            this.selectedEnvironment = environments.get(0);
        }
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

    public List<Environment> environments() {
        return environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public Environment selectedEnvironment() {
        return selectedEnvironment;
    }

    public void setSelectedEnvironment(Environment selectedEnvironment) {
        this.selectedEnvironment = selectedEnvironment;
        DaakiaSettings settings = DaakiaSettings.getInstance();
        if(selectedEnvironment != null && selectedEnvironment.getId() != null) {
            settings.getState().lastEnvironmentId = selectedEnvironment.getId();
        } else if(selectedEnvironment == null) {
            settings.getState().lastEnvironmentId = -1;
        }
    }
}
