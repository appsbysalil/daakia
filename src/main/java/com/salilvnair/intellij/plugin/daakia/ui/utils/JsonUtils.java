package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaStoreRecord;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static MultiValueMap<String, String> jsonStringToMultivaluedMap(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(jsonString == null || jsonString.isEmpty()) {
                return new LinkedMultiValueMap<>();
            }
            // Deserialize JSON string into Map<String, List<String>>
            LinkedHashMap<String, List<String>> map = objectMapper.readValue(jsonString, new TypeReference<>() {});

            // Convert Map<String, List<String>> to MultiValuedMap<String, String>
            MultiValueMap<String, String> multivaluedMap = new LinkedMultiValueMap<>();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                multivaluedMap.addAll(entry.getKey(), entry.getValue());
            }
            return multivaluedMap;
        }
        catch (IOException e) {
            return new LinkedMultiValueMap<>();
        }
    }

    public static String convertTreeToJSON(DefaultMutableTreeNode rootNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = convertTreeToJSON(mapper.createObjectNode(), rootNode, mapper);
        return json.toString();
    }

    public static ObjectNode convertTreeToJSON(ObjectNode parentNode, DefaultMutableTreeNode node, ObjectMapper mapper) {
        // Add the current node's user object as a property
        parentNode.put("name", node.getUserObject().toString());
        if(node.isLeaf()) {
            if(node.getUserObject() instanceof DaakiaStoreRecord daakiaStoreRecord) {
                try {
                    String daakiaStoreRecordString = pojoToJson(daakiaStoreRecord);
                    parentNode.put("record", daakiaStoreRecordString);
                }
                catch (IOException e) {}
            }
        }

        // Recursively process child nodes
        if (node.getChildCount() > 0) {
            ArrayNode children = mapper.createArrayNode();
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                ObjectNode childJson = mapper.createObjectNode();
                convertTreeToJSON(childJson, childNode, mapper);
                children.add(childJson);
            }
            parentNode.set("children", children);
        }

        return parentNode;
    }
}
