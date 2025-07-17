package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        if (env.getVariables() != null) {
            for (Variable v : env.getVariables()) {
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

    public static String resolveVariables(String text, Environment env) {
        if(text == null || env == null) {
            return text;
        }
        for(Variable v : env.getVariables()) {
            if(v.getKey() != null) {
                String placeholder = "{{" + v.getKey() + "}}";
                String val = v.getCurrentValue()!=null ? v.getCurrentValue() : v.getInitialValue();
                text = text.replace(placeholder, val != null ? val : "");

            }
        }
        return text;
    }
}
