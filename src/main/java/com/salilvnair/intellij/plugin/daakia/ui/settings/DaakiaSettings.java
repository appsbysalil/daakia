package com.salilvnair.intellij.plugin.daakia.ui.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
@State(name = "DaakiaSettings", storages = @Storage("daakia.xml"))
public final class DaakiaSettings implements PersistentStateComponent<DaakiaSettingsState> {
    private DaakiaSettingsState state = new DaakiaSettingsState();

    @Override
    public @Nullable DaakiaSettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull DaakiaSettingsState state) {
        this.state = state;
    }

    public static DaakiaSettings getInstance() {
        return com.intellij.openapi.application.ApplicationManager.getApplication().getService(DaakiaSettings.class);
    }
}
