package com.salilvnair.intellij.plugin.daakia.ui.service.type;

import java.util.Arrays;

public enum RequestType {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    GRAPHQL("GRAPHQL"),
    ;

    private final String type;

    RequestType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public static RequestType findByType(String requestType) {
        return Arrays.stream(RequestType.values()).filter(rt -> rt.type().equals(requestType)).findFirst().orElse(GET);
    }

}
