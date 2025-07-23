package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.EnvironmentTemplate;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for converting Postman environment definitions into
 * Daakia's environment model.
 */
public final class PostmanEnvironmentUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    private PostmanEnvironmentUtils() {}

    /**
     * Parse a Postman environment JSON string into an {@link Environment} object.
     */
    public static Environment fromPostmanJson(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        Environment env = new Environment();
        env.setName(root.path("name").asText("Imported"));
        List<Variable> vars = new ArrayList<>();
        JsonNode values = root.path("values");
        if (values.isArray()) {
            for (JsonNode node : values) {
                Variable v = new Variable();
                v.setKey(node.path("key").asText());
                String value = node.path("value").asText();
                v.setInitialValue(value);
                v.setCurrentValue(value);
                v.setType(node.path("type").asText("default"));
                vars.add(v);
            }
        }
        env.setVariables(vars);
        return env;
    }

    /**
     * Convert a {@link Environment} into a Postman environment JSON string.
     */
    public static String toPostmanJson(Environment env) throws IOException {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("name", env.getName());
        List<Map<String, Object>> values = new ArrayList<>();
        if (env.variables() != null) {
            for (Variable v : env.variables()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("key", v.getKey());
                String value = v.getCurrentValue() != null ? v.getCurrentValue() : v.getInitialValue();
                map.put("value", value);
                if (v.getType() != null) {
                    map.put("type", v.getType());
                }
                values.add(map);
            }
        }
        root.put("values", values);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    public static String resolveVariables(String text, DataContext dataContext) {
        if (text == null) {
            return null;
        }

        EnvironmentTemplate selectedEnv = dataContext.globalContext().selectedEnvironment();
        EnvironmentTemplate globalEnv = dataContext.globalContext().getGlobalEnvironment();

        if (selectedEnv == null && globalEnv == null) {
            return text;
        }

        // Combined variable lookup with fallback to global
        return resolveWithFallback(text, selectedEnv, globalEnv);
    }

    private static String resolveWithFallback(String text, EnvironmentTemplate selected, EnvironmentTemplate global) {
        // Collect selected and global variables into maps
        Map<String, String> selectedVars = toVariableMap(selected);
        Map<String, String> globalVars = toVariableMap(global);

        Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
        Matcher matcher = pattern.matcher(text);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = selectedVars.containsKey(key)
                    ? selectedVars.get(key)
                    : globalVars.getOrDefault(key, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value : ""));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static Map<String, String> toVariableMap(EnvironmentTemplate env) {
        Map<String, String> map = new HashMap<>();
        if (env != null && env.variables() != null) {
            for (Variable v : env.variables()) {
                if (v.getKey() != null) {
                    String val = v.getCurrentValue() != null ? v.getCurrentValue() : v.getInitialValue();
                    map.put(v.getKey(), val != null ? val : "");
                }
            }
        }
        return map;
    }
}
