package com.salilvnair.intellij.plugin.daakia.script.factory;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Map;

public class PostmanResponseFactory {

    public static Value create(Context context, String body, int status, Map<String, String> headers) {
        // JS function to simulate full response object
        String script = """
            (function(body, status, headers) {
                // Normalize headers to lower-case keys
                const normalizedHeaders = {};
                for (const key in headers) {
                    normalizedHeaders[key.toLowerCase()] = headers[key];
                }

                // Parse cookies (basic version from 'set-cookie')
                const cookies = {};
                if (normalizedHeaders['set-cookie']) {
                    const cookieLines = Array.isArray(normalizedHeaders['set-cookie']) 
                        ? normalizedHeaders['set-cookie'] 
                        : [normalizedHeaders['set-cookie']];
                    cookieLines.forEach(line => {
                        const parts = line.split(';')[0].split('=');
                        if (parts.length === 2) {
                            cookies[parts[0].trim()] = parts[1].trim();
                        }
                    });
                }

                return {
                    status: status,
                    headers: normalizedHeaders,
                    body: body,
                    cookies: cookies,
                    json: function() {
                        try {
                            return JSON.parse(body);
                        } catch (e) {
                            throw new Error("Invalid JSON: " + e.message);
                        }
                    },
                    text: function() {
                        return body;
                    },
                    header: function(name) {
                        return normalizedHeaders[name.toLowerCase()] || null;
                    },
                    toString: function() {
                        return body;
                    }
                };
            })
        """;

        Value fn = context.eval("js", script);

        // Convert headers map to JS object
        Value jsHeaders = context.eval("js", "({})");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            jsHeaders.putMember(entry.getKey(), entry.getValue());
        }

        return fn.execute(body, status, jsHeaders);
    }
}
