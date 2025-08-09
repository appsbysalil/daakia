package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreCollection;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.AuthInfo;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AuthorizationType;
import org.jetbrains.annotations.NotNull;
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
        DaakiaStoreCollection collection = prepareStoreCollection(root.path("info").path("name").asText("Imported Collection"));
        store.setCollection(collection);
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

    private static @NotNull DaakiaStoreCollection prepareStoreCollection(String name) {
        DaakiaStoreCollection collection = new DaakiaStoreCollection();
        collection.setCollectionName(name);
        collection.setCollection(true);
        return collection;
    }

    private static DaakiaStore parseItem(JsonNode itemNode) throws IOException {
        DaakiaStore store = new DaakiaStore();
        store.setName(itemNode.path("name").asText());
        DaakiaStoreCollection collection = prepareStoreCollection(itemNode.path("name").asText());
        store.setCollection(collection);
        if (itemNode.has("item")) {
            collection.setCollection(true);
            List<DaakiaStore> children = new ArrayList<>();
            for (JsonNode child : itemNode.get("item")) {
                children.add(parseItem(child));
            }
            store.setChildren(children);
        }
        else {
            collection.setCollection(false);
            DaakiaStoreRecord record = new DaakiaStoreRecord();
            record.setUuid(UUID.randomUUID().toString());
            record.setDisplayName(store.getName());
            record.setCreatedDate(DateUtils.todayAsString());
            JsonNode req = itemNode.get("request");
            if (req != null) {
                record.setRequestType(req.path("method").asText());
                JsonNode urlNode = req.path("url");
                if (urlNode.isTextual()) {
                    record.setUrl(urlNode.asText());
                } else {
                    record.setUrl(urlNode.path("raw").asText());
                }
                Map<String, List<String>> headerMap = new LinkedHashMap<>();
                AuthInfo authInfo = null;
                if (req.has("header")) {
                    for (JsonNode h : req.get("header")) {
                        String key = h.path("key").asText();
                        String value = h.path("value").asText();
                        if ("Authorization".equalsIgnoreCase(key)) {
                            if (authInfo == null) {
                                authInfo = parseAuthorizationHeader(value);
                            }
                        } else {
                            headerMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                        }
                    }
                }
                JsonNode authNode = req.path("auth");
                if (authNode != null && authNode.has("type")) {
                    String type = authNode.path("type").asText();
                    if ("bearer".equalsIgnoreCase(type)) {
                        String token = null;
                        JsonNode bearerNode = authNode.path("bearer");
                        if (bearerNode.isArray() && bearerNode.size() > 0) {
                            token = bearerNode.get(0).path("value").asText();
                        } else {
                            token = bearerNode.path("token").asText();
                        }
                        if (token != null) {
                            authInfo = new AuthInfo();
                            authInfo.setAuthType(AuthorizationType.BEARER_TOKEN.type());
                            authInfo.setToken(token);
                        }
                    } else if ("basic".equalsIgnoreCase(type)) {
                        String user = "";
                        String pass = "";
                        JsonNode basicNode = authNode.path("basic");
                        if (basicNode.isArray()) {
                            for (JsonNode b : basicNode) {
                                if ("username".equalsIgnoreCase(b.path("key").asText())) {
                                    user = b.path("value").asText();
                                } else if ("password".equalsIgnoreCase(b.path("key").asText())) {
                                    pass = b.path("value").asText();
                                }
                            }
                        } else {
                            user = basicNode.path("username").asText();
                            pass = basicNode.path("password").asText();
                        }
                        authInfo = new AuthInfo();
                        authInfo.setAuthType(AuthorizationType.BASIC_AUTH.type());
                        authInfo.setUsername(user);
                        authInfo.setPassword(pass);
                    }
                }
                if (!headerMap.isEmpty()) {
                    record.setHeaders(CryptoUtils.encrypt(JsonUtils.pojoToJson(headerMap)));
                }
                if (authInfo != null) {
                    record.setAuthInfo(JsonUtils.pojoToJson(authInfo));
                }
                JsonNode bodyNode = req.path("body");
                if (bodyNode != null && bodyNode.has("raw")) {
                    record.setRequestBody(bodyNode.path("raw").asText());
                }
            }
            if (itemNode.has("event")) {
                for (JsonNode ev : itemNode.get("event")) {
                    String listen = ev.path("listen").asText();
                    JsonNode exec = ev.path("script").path("exec");
                    String script = null;
                    if (exec.isArray()) {
                        List<String> lines = new ArrayList<>();
                        for (JsonNode ln : exec) { lines.add(ln.asText()); }
                        script = String.join("\n", lines);
                    } else if (exec.isTextual()) {
                        script = exec.asText();
                    }
                    if ("prerequest".equalsIgnoreCase(listen)) {
                        record.setPreRequestScript(script);
                    } else if ("test".equalsIgnoreCase(listen)) {
                        record.setPostRequestScript(script);
                    }
                }
            }
            store.setRecord(record);
        }
        return store;
    }

    private static AuthInfo parseAuthorizationHeader(String value) {
        if (value == null) {
            return null;
        }
        AuthInfo authInfo = null;
        if (value.startsWith(AuthorizationType.Constant.BEARER_SPACE)) {
            authInfo = new AuthInfo();
            authInfo.setAuthType(AuthorizationType.BEARER_TOKEN.type());
            authInfo.setToken(value.substring(7));
        }
        else if (value.startsWith(AuthorizationType.Constant.BASIC_SPACE)) {
            try {
                String decoded = new String(Base64.getDecoder().decode(value.substring(6)));
                String[] parts = decoded.split(":",2);
                authInfo = new AuthInfo();
                authInfo.setAuthType(AuthorizationType.BASIC_AUTH.type());
                if(parts.length>0) authInfo.setUsername(parts[0]);
                if(parts.length>1) authInfo.setPassword(parts[1]);
            } catch (Exception ignore) {}
        }
        return authInfo;
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
        if (store.ofTypeCollection()) {
            List<Object> children = new ArrayList<>();
            if (store.getChildren() != null) {
                for (DaakiaStore child : store.getChildren()) {
                    children.add(convertStore(child));
                }
            }
            map.put("item", children);
        }
        else if (store.getRecord() != null) {
            DaakiaStoreRecord r = store.getRecord();
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("method", r.getRequestType());
            Map<String, Object> url = new LinkedHashMap<>();
            url.put("raw", r.getUrl());
            request.put("url", url);
            Map<String, List<String>> headers = null;
            AuthInfo authInfo = null;
            if (r.getAuthInfo() != null) {
                authInfo = JsonUtils.jsonToPojo(r.getAuthInfo(), AuthInfo.class);
            }
            if (r.getHeaders() != null && !r.getHeaders().isEmpty()) {
                String hdr = CryptoUtils.decrypt(r.getHeaders());
                headers = JsonUtils.jsonToPojo(hdr, new TypeReference<>() {});
                if(headers.containsKey("Authorization") && authInfo == null) {
                    authInfo = parseAuthorizationHeader(headers.get("Authorization").get(0));
                }
                headers.remove("Authorization");
                List<Map<String, String>> headerList = new ArrayList<>();
                for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                    for (String val : e.getValue()) {
                        Map<String, String> hv = new LinkedHashMap<>();
                        hv.put("key", e.getKey());
                        hv.put("value", val);
                        headerList.add(hv);
                    }
                }
                if(!headerList.isEmpty()) {
                    request.put("header", headerList);
                }
            }
            if (authInfo != null) {
                Map<String, Object> auth = new LinkedHashMap<>();
                if (AuthorizationType.BEARER_TOKEN.type().equals(authInfo.getAuthType())) {
                    auth.put("type", "bearer");
                    List<Map<String, String>> bearer = new ArrayList<>();
                    Map<String, String> token = new LinkedHashMap<>();
                    token.put("key", "token");
                    token.put("value", authInfo.getToken());
                    bearer.add(token);
                    auth.put("bearer", bearer);
                } else if (AuthorizationType.BASIC_AUTH.type().equals(authInfo.getAuthType())) {
                    auth.put("type", "basic");
                    List<Map<String, String>> basic = new ArrayList<>();
                    Map<String, String> u = new LinkedHashMap<>();
                    u.put("key", "username");
                    u.put("value", authInfo.getUsername() != null ? authInfo.getUsername() : "");
                    basic.add(u);
                    Map<String, String> p = new LinkedHashMap<>();
                    p.put("key", "password");
                    p.put("value", authInfo.getPassword() != null ? authInfo.getPassword() : "");
                    basic.add(p);
                    auth.put("basic", basic);
                }
                if(!auth.isEmpty()) {
                    request.put("auth", auth);
                }
            }
            if (r.getRequestBody() != null && !r.getRequestBody().isEmpty()) {
                Map<String, String> body = new LinkedHashMap<>();
                body.put("mode", "raw");
                body.put("raw", r.getRequestBody());
                request.put("body", body);
            }
            List<Map<String,Object>> events = new ArrayList<>();
            if (r.getPreRequestScript() != null && !r.getPreRequestScript().isEmpty()) {
                Map<String,Object> ev = new LinkedHashMap<>();
                ev.put("listen", "prerequest");
                Map<String,Object> script = new LinkedHashMap<>();
                script.put("type", "text/javascript");
                script.put("exec", Arrays.asList(r.getPreRequestScript().split("\n")));
                ev.put("script", script);
                events.add(ev);
            }
            if (r.getPostRequestScript() != null && !r.getPostRequestScript().isEmpty()) {
                Map<String,Object> ev = new LinkedHashMap<>();
                ev.put("listen", "test");
                Map<String,Object> script = new LinkedHashMap<>();
                script.put("type", "text/javascript");
                script.put("exec", Arrays.asList(r.getPostRequestScript().split("\n")));
                ev.put("script", script);
                events.add(ev);
            }
            if(!events.isEmpty()) {
                map.put("event", events);
            }
            map.put("request", request);
        }
        return map;
    }
}
