package com.salilvnair.intellij.plugin.daakia.ui.service.type;

import java.util.Arrays;

public enum AuthorizationType {
    NONE("None"),
    BASIC_AUTH("Basic Auth"),
    BEARER_TOKEN("Bearer Token"),
    ;



    public static class Constant {
        public static final String BEARER_SPACE = "Bearer ";
        public static final String BASIC_SPACE = "Basic ";
        public static final String AUTHORIZATION = "Authorization";
        public static final String AUTHORIZATION_TYPE = "Authorization Type";
        public static final String USERNAME = "Username";
        public static final String PASSWORD = "Password";
        public static final String TOKEN = "Token";
    }

    private final String type;

    AuthorizationType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }

    public static AuthorizationType findByType(String requestType) {
        return Arrays.stream(AuthorizationType.values()).filter(rt -> rt.type().equals(requestType)).findFirst().orElse(NONE);
    }

    public static String[] types() {
        return Arrays.stream(AuthorizationType.values()).map(authorizationType -> authorizationType.type).toArray(String[]::new);
    }

}
