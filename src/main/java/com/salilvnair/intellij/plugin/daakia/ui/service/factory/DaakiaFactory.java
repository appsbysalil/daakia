package com.salilvnair.intellij.plugin.daakia.ui.service.factory;

import com.salilvnair.intellij.plugin.daakia.ui.service.core.DaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.provider.AppDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.provider.StoreDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.provider.RestDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;

public final class DaakiaFactory {

    private DaakiaFactory() {}

    public static DaakiaService generate(DaakiaType daakiaType) {
        if (DaakiaType.REST.equals(daakiaType)) {
            return new RestDaakiaService();
        }
        else if (DaakiaType.STORE.equals(daakiaType)) {
            return new StoreDaakiaService();
        }
        else if (DaakiaType.APP.equals(daakiaType)) {
            return new AppDaakiaService();
        }
        return null;
    }
}
