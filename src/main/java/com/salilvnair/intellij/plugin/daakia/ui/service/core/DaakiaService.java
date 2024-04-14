package com.salilvnair.intellij.plugin.daakia.ui.service.core;

import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;

public interface DaakiaService {
    DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects);
}
