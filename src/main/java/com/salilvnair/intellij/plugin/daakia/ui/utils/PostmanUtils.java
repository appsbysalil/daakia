package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.utils.CryptoUtils;

import java.io.IOException;
import java.util.*;

/**
 * Utility methods for converting between Postman v2 collections and Daakia's data model.
 */
public final class PostmanUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    private PostmanUtils() {}

    /**
     * Parse a Postman v2 collection JSON string into a {@link DaakiaStore} hierarchy.
     */
    public static DaakiaStore fromPostmanJson(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        DaakiaStore store = new DaakiaStore();
        store.setName(root.path("info").path("name").asText("Imported"));
        store.setCollection(true);
        List<DaakiaStore> children = new ArrayList<>();
        JsonNode items = root.path("item");
        if (items.isArray()) {
            for (JsonNode item : items) {
                children.add(parseItem(item));
            }
        }
        store.setChildren(children);
        return store;
    }

    private static DaakiaStore parseItem(JsonNode itemNode) throws IOException {
        DaakiaStore store = new DaakiaStore();
        store.setName(itemNode.path("name").asText());
        if (itemNode.has("item")) {
            store.setCollection(true);
            List<DaakiaStore> children = new ArrayList<>();
            for (JsonNode child : itemNode.get("item")) {
                children.add(parseItem(child));
            }
            store.setChildren(children);
        } else {
            store.setCollection(false);
            DaakiaStoreRecord record = new DaakiaStoreRecord();
            record.setUuid(UUID.randomUUID().toString());
            record.setDisplayName(store.getName());
            JsonNode req = itemNode.get("request");
            if (req != null) {
                record.setRequestType(req.path("method").asText());
                JsonNode urlNode = req.path("url");
                if (urlNode.isTextual()) {
                    record.setUrl(urlNode.asText());
                } else {
                    record.setUrl(urlNode.path("raw").asText());
                }
                if (req.has("header")) {
                    Map<String, List<String>> headerMap = new LinkedHashMap<>();
                    for (JsonNode h : req.get("header")) {
                        String key = h.path("key").asText();
                        String value = h.path("value").asText();
                        headerMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    }
                    record.setHeaders(CryptoUtils.encrypt(JsonUtils.pojoToJson(headerMap)));
                }
                JsonNode bodyNode = req.path("body");
                if (bodyNode != null && bodyNode.has("raw")) {
                    record.setRequestBody(bodyNode.path("raw").asText());
                }
            }
            store.setRecord(record);
        }
        return store;
    }

    /**
     * Convert a {@link DaakiaStore} hierarchy into a Postman v2 collection JSON string.
     */
    public static String toPostmanJson(DaakiaStore store) throws IOException {
        Map<String, Object> root = new LinkedHashMap<>();
        Map<String, String> info = new LinkedHashMap<>();
        info.put("name", store.getName());
        info.put("schema", "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
        root.put("info", info);
        List<Object> items = new ArrayList<>();
        if (store.getChildren() != null) {
            for (DaakiaStore child : store.getChildren()) {
                items.add(convertStore(child));
            }
        }
        root.put("item", items);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    private static Map<String, Object> convertStore(DaakiaStore store) throws IOException {
        Map<String, Object> map = new LinkedHashMap<>();
        String name = store.getRecord() != null && store.getRecord().getDisplayName() != null
                ? store.getRecord().getDisplayName() : store.getName();
        map.put("name", name);
        if (store.isCollection()) {
            List<Object> children = new ArrayList<>();
            if (store.getChildren() != null) {
                for (DaakiaStore child : store.getChildren()) {
                    children.add(convertStore(child));
                }
            }
            map.put("item", children);
        } else if (store.getRecord() != null) {
            DaakiaStoreRecord r = store.getRecord();
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("method", r.getRequestType());
            Map<String, Object> url = new LinkedHashMap<>();
            url.put("raw", r.getUrl());
            request.put("url", url);
            if (r.getHeaders() != null && !r.getHeaders().isEmpty()) {
                String hdr = CryptoUtils.decrypt(r.getHeaders());
                Map<String, List<String>> headers = JsonUtils.jsonToPojo(hdr, new TypeReference<>() {});
                List<Map<String, String>> headerList = new ArrayList<>();
                for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                    for (String val : e.getValue()) {
                        Map<String, String> hv = new LinkedHashMap<>();
                        hv.put("key", e.getKey());
                        hv.put("value", val);
                        headerList.add(hv);
                    }
                }
                request.put("header", headerList);
            }
            if (r.getRequestBody() != null && !r.getRequestBody().isEmpty()) {
                Map<String, String> body = new LinkedHashMap<>();
                body.put("mode", "raw");
                body.put("raw", r.getRequestBody());
                request.put("body", body);
            }
            map.put("request", request);
        }
        return map;
    }
}
