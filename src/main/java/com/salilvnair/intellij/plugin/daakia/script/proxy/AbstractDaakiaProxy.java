package com.salilvnair.intellij.plugin.daakia.script.proxy;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.GlobalEnvironment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDaakiaProxy implements ProxyObject {
    protected GlobalEnvironment globalEnvironment;
    protected Environment environment;

    protected AbstractDaakiaProxy(DataContext dataContext) {
        this.globalEnvironment = dataContext.globalContext().globalEnvironment();
        this.environment = dataContext.globalContext().selectedEnvironment();
    }

    @Override
    public void putMember(String key, Value value) {
        Object val = value.as(Object.class);
        try {
            Field field = this.getClass().getField(key);
            field.set(this, val);
        } catch (Exception ignored) {}
        if (val != null) {
            setGlobalEnvVariable(key, val.toString());
        }
    }

    protected void setGlobalEnvVariable(String key, String value) {
        if(globalEnvironment == null) {
            globalEnvironment = new GlobalEnvironment();
        }
        Variable var = null;
        for (Variable v : globalEnvironment.variables()) {
            if (key.equals(v.getKey())) {
                var = v;
                break;
            }
        }
        if (var == null) {
            var = new Variable();
            var.setKey(key);
            globalEnvironment.variables().add(var);
        }
        var.setCurrentValue(value);
    }

    protected String getGlobalEnvVariable(String key) {
        if(globalEnvironment == null) {
            globalEnvironment = new GlobalEnvironment();
        }
        for (Variable v : globalEnvironment.variables()) {
            if (key.equals(v.getKey())) {
                return v.getCurrentValue();
            }
        }
        return null;
    }

    protected void setEnvVariable(String key, String value) {
        if(environment == null) {
            return;
        }
        Variable var = null;
        for (Variable v : environment.variables()) {
            if (key.equals(v.getKey())) {
                var = v;
                break;
            }
        }
        if (var == null) {
            var = new Variable();
            var.setKey(key);
            environment.variables().add(var);
        }
        var.setCurrentValue(value);
    }

    protected String getEnvVariable(String key) {
        if(environment == null) {
            return null;
        }
        for (Variable v : environment.variables()) {
            if (key.equals(v.getKey())) {
                return v.getCurrentValue();
            }
        }
        return null;
    }

    @Override
    public boolean hasMember(String key) {
        return true;
    }

    @Override
    public boolean removeMember(String key) {
        if (environment == null) {
            return false;
        }
        environment.variables().removeIf(v -> key.equals(v.getKey()));
        return true;
    }

    @Override
    public Set<String> getMemberKeys() {
        if (environment == null) {
            return new HashSet<>();
        }
        Set<String> keys = new HashSet<>();
        for (Variable v : environment.variables()) {
            keys.add(v.getKey());
        }
        return keys;
    }
}
