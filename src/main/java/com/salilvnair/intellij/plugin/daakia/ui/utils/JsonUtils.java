package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> T jsonToPojo(String json, Class<T> valueType) throws IOException {
        return objectMapper.readValue(json, valueType);
    }

    public static <T> T jsonToPojo(String json, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(json, typeReference);
    }

    public static <T> String pojoToJson(T pojo) throws IOException {
        return objectMapper.writeValueAsString(pojo);
    }


    public static void writeJsonToFile(String json, String filePath) throws IOException {
        Files.write(Paths.get(filePath), json.getBytes());
    }

    public static String readJsonFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            return "{}"; // Return empty JSON object if file didn't exist
        }
        else {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        }
    }
}
