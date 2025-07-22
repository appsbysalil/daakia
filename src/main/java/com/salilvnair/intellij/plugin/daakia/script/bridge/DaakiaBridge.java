package com.salilvnair.intellij.plugin.daakia.script.bridge;

import com.salilvnair.intellij.plugin.daakia.script.factory.DaakiaPmFactory;
import com.salilvnair.intellij.plugin.daakia.script.proxy.AbstractDaakiaProxy;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import lombok.Getter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DaakiaBridge extends AbstractDaakiaProxy {
    @Getter
    private Environment environment;

    private final Context context;
    private final DataContext dataContext;

    public DaakiaBridge(Context context, DataContext dataContext) {
        super(dataContext);
        this.context = context;
        this.dataContext = dataContext;
    }

    @Override
    public Object getMember(String key) {
        if ("pm".equals(key)) {
            return new DaakiaPmFactory(context, dataContext);
        }

        try {
            Method method = this.getClass().getMethod(key, String.class);
            return (ProxyExecutable) args -> {
                try {
                    return method.invoke(this, args[0].asString());
                } catch (Exception e) {
                    return "ERROR: " + e.getMessage();
                }
            };
        } catch (NoSuchMethodException ignored) {}

        try {
            Field field = this.getClass().getField(key);
            return field.get(this);
        } catch (Exception ignored) {}

        return null;
    }
}

