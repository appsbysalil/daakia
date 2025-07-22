package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Utility class for executing JavaScript snippets typed in Daakia's
 * script editors. Scripts get access to a global {@code daakia} object
 * with a {@code setGlobalEnvVar(name, value)} method for updating the
 * currently selected environment variables.
 */
public final class DaakiaScriptExecutor {
    private static final ScriptEngine ENGINE = new NashornScriptEngineFactory().getScriptEngine();

    private DaakiaScriptExecutor() {}

    public static void execute(String script, Environment env) {
        if (script == null || env == null) {
            return;
        }
        Bindings bindings = ENGINE.createBindings();
        bindings.put("daakia", new ScriptBridge(env));
        try {
            ENGINE.eval(script, bindings);
        } catch (ScriptException ignored) {
        }
    }

    /**
     * Bridge object exposed to JavaScript as {@code daakia}.
     */
    public static class ScriptBridge {
        private final Environment env;
        public ScriptBridge(Environment env) {
            this.env = env;
        }

        /**
         * Set or create a variable in the provided environment.
         */
        public void setGlobalEnvVar(String key, String value) {
            if (key == null) {
                return;
            }
            Variable var = null;
            for (Variable v : env.variables()) {
                if (key.equals(v.getKey())) {
                    var = v;
                    break;
                }
            }
            if (var == null) {
                var = new Variable();
                var.setKey(key);
                env.variables().add(var);
            }
            var.setCurrentValue(value);
        }
    }
}
