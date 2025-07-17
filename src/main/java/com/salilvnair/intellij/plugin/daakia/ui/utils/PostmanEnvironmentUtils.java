package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
}
