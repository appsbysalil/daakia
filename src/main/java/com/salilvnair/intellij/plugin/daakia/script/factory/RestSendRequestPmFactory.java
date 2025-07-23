package com.salilvnair.intellij.plugin.daakia.script.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salilvnair.intellij.plugin.daakia.script.helper.GraalValueConverter;
import org.graalvm.polyglot.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RestSendRequestPmFactory {

    public static String buildRequestBody(Value bodyVal) {
        if (bodyVal == null) return "";

        try {
            if (bodyVal.hasMember("mode")) {
                String mode = bodyVal.getMember("mode").asString();
                return switch (mode) {
                    case "urlencoded" -> buildUrlEncodedBody(bodyVal);
                    case "raw" -> buildRawBody(bodyVal);
                    case "formdata" -> buildFormDataBody(bodyVal);
                    default -> "";
                };
            } else if (bodyVal.hasMembers()) {
                // Direct JSON object, no "mode"
                Object converted = GraalValueConverter.convert(bodyVal); // from earlier message
                return new ObjectMapper().writeValueAsString(converted);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String buildUrlEncodedBody(Value bodyVal) {
        if (!bodyVal.hasMember("urlencoded")) return "";

        Value arr = bodyVal.getMember("urlencoded");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.getArraySize(); i++) {
            Value pair = arr.getArrayElement(i);
            String key = pair.getMember("key").asString();
            String value = pair.getMember("value").asString();
            sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            if (i < arr.getArraySize() - 1) sb.append("&");
        }
        return sb.toString();
    }

    private static String buildRawBody(Value bodyVal) {
        if (bodyVal.hasMember("raw")) {
            return bodyVal.getMember("raw").asString();
        }
        return "";
    }

    private static String buildFormDataBody(Value bodyVal) {
        if (!bodyVal.hasMember("formdata")) return "";

        Value arr = bodyVal.getMember("formdata");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.getArraySize(); i++) {
            Value item = arr.getArrayElement(i);
            String key = item.getMember("key").asString();
            String value = item.getMember("value").asString(); // No file support yet
            sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            if (i < arr.getArraySize() - 1) sb.append("&");
        }
        return sb.toString();
    }
}
