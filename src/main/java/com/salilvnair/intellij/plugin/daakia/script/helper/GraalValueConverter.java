package com.salilvnair.intellij.plugin.daakia.script.helper;
import org.graalvm.polyglot.Value;

import java.util.*;

public class GraalValueConverter {
    public static Object convert(Value value) {
        if (value == null) return null;
        if (value.isString()) return value.asString();
        if (value.isNumber()) return value.as(Number.class);
        if (value.isBoolean()) return value.asBoolean();
        if (value.hasArrayElements()) {
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < value.getArraySize(); i++) {
                list.add(convert(value.getArrayElement(i)));
            }
            return list;
        }
        if (value.hasMembers()) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (String key : value.getMemberKeys()) {
                map.put(key, convert(value.getMember(key)));
            }
            return map;
        }
        return value.toString(); // Fallback
    }
}
