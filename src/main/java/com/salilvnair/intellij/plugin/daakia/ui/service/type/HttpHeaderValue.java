package com.salilvnair.intellij.plugin.daakia.ui.service.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HttpHeaderValue {
    APPLICATION_JSON("application/json"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_XHTML("application/xhtml+xml"),
    APPLICATION_XML("application/xml"),
    APPLICATION_ZSTD("application/zstd"),
    ATTACHMENT("attachment"),
    BASE64("base64"),
    BINARY("binary"),
    BOUNDARY("boundary"),
    BYTES("bytes"),
    CHARSET("charset"),
    CHUNKED("chunked"),
    CLOSE("close"),
    COMPRESS("compress"),
    CONTINUE("100-continue"),
    DEFLATE("deflate"),
    X_DEFLATE("x-deflate"),
    FILE("file"),
    FILENAME("filename"),
    FORM_DATA("form-data"),
    GZIP("gzip"),
    BR("br"),
    SNAPPY("snappy"),
    ZSTD("zstd"),
    GZIP_DEFLATE("gzip,deflate"),
    X_GZIP("x-gzip"),
    IDENTITY("identity"),
    KEEP_ALIVE("keep-alive"),
    MAX_AGE("max-age"),
    MAX_STALE("max-stale"),
    MIN_FRESH("min-fresh"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    MULTIPART_MIXED("multipart/mixed"),
    MUST_REVALIDATE("must-revalidate"),
    NAME("name"),
    NO_CACHE("no-cache"),
    NO_STORE("no-store"),
    NO_TRANSFORM("no-transform"),
    NONE("none"),
    ZERO("0"),
    ONLY_IF_CACHED("only-if-cached"),
    PRIVATE("private"),
    PROXY_REVALIDATE("proxy-revalidate"),
    PUBLIC("public"),
    QUOTED_PRINTABLE("quoted-printable"),
    S_MAXAGE("s-maxage"),
    TEXT_CSS("text/css"),
    TEXT_HTML("text/html"),
    TEXT_EVENT_STREAM("text/event-stream"),
    TEXT_PLAIN("text/plain"),
    TRAILERS("trailers"),
    UPGRADE("upgrade"),
    WEBSOCKET("websocket"),
    XML_HTTP_REQUEST("XMLHttpRequest");

    private final String value;

    HttpHeaderValue(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /** Returns all header values as list */
    public static List<String> headerValues() {
        return Arrays.stream(values())
                .map(HttpHeaderValue::value)
                .collect(Collectors.toList());
    }
}
