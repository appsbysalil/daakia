package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.core.Publisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.provider.DaakiaGlobalEventPublisher;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.persistence.EnvironmentDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.GlobalEnvironment;
import com.salilvnair.intellij.plugin.daakia.ui.settings.DaakiaSettings;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

@Getter
@Setter
public class GlobalContext {
    private SideNavContext sideNavContext;
    private Publisher<EventObject> publisher;
    private DaakiaGlobalEventPublisher globalEventPublisher;
    private List<Environment> environments;
    private GlobalEnvironment globalEnvironment;
    private Environment selectedEnvironment;

    public GlobalContext() {
        this.sideNavContext = new SideNavContext();
        this.publisher = new Publisher<>();
        this.globalEventPublisher = new DaakiaGlobalEventPublisher(publisher);
        // Load environments synchronously during initialization to avoid race
        // conditions between the UI and persistence layer.
        this.environments = new EnvironmentDao().loadEnvironments();
        globalEventPublisher().onEnvironmentListChanged();
        int envId = DaakiaSettings.getInstance().getState().lastEnvironmentId;
        if (envId > 0 && envId <= environments.size()) {
            this.selectedEnvironment = environments.get(envId - 1);
        }
        else if (!environments.isEmpty()) {
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
        if(environments == null) {
            environments = new ArrayList<>();
        }
        return environments;
    }


    public Environment selectedEnvironment() {
        return selectedEnvironment;
    }

    public GlobalEnvironment globalEnvironment() {
        if(globalEnvironment == null) {
            globalEnvironment = new GlobalEnvironment();
        }
        return globalEnvironment;
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
