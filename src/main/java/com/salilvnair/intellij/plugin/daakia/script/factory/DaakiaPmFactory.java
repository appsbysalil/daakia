package com.salilvnair.intellij.plugin.daakia.script.factory;

import com.salilvnair.intellij.plugin.daakia.script.proxy.AbstractDaakiaProxy;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class DaakiaPmFactory extends AbstractDaakiaProxy {

    private final Context context;
    private final DataContext dataContext;

    public DaakiaPmFactory(Context context, DataContext dataContext) {
        super(dataContext);
        this.context = context;
        this.dataContext = dataContext;
    }

    @Override
    public Object getMember(String memberKey) {
        if ("environment".equals(memberKey) || "globals".equals(memberKey)) {
            return new DaakiaEnvAccessor(memberKey, dataContext);
        }

        if ("testToken".equals(memberKey)) {
            return (ProxyExecutable) args -> {
                if (args.length == 1) {
                    String token = args[0].asString();
                    System.out.println("🟢 Daakia: Test token received: " + token);
                    // Here you can add logic to validate or use the token
                } else {
                    System.out.println("🔴 Daakia: Invalid number of arguments for testToken");
                }
                return null;
            };
        }

        if ("sendRequest".equals(memberKey)) {
            return (ProxyExecutable) args -> {
                if (args.length == 2) {
                    Value options = args[0];
                    Value callback = args[1];

                    String url = options.getMember("url").asString();
                    String method = options.hasMember("method") ? options.getMember("method").asString() : "GET";
                    String body = "";
                    if (options.hasMember("body")) {
                        body = RestSendRequestPmFactory.buildRequestBody(options.getMember("body"));
                    }
                    HttpHeaders headers = new HttpHeaders();
                    if (options.hasMember("headers")) {
                        Value headerObj = options.getMember("headers");
                        for (String key : headerObj.getMemberKeys()) {
                            headers.put(key, headerObj.getMember(key).asString());
                        }
                    }

                    try {
                        System.out.println("🟡 Daakia: calling URL = " + url);
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setRequestMethod(method.toUpperCase());

                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            conn.setRequestProperty(entry.getKey(), entry.getValue());
                        }

                        if (!"GET".equalsIgnoreCase(method) && !body.isEmpty()) {
                            conn.setDoOutput(true);
                            try (OutputStream os = conn.getOutputStream()) {
                                os.write(body.getBytes());
                            }
                        }

                        int code = conn.getResponseCode();
                        Scanner scanner = new Scanner(
                                code >= 400 ? conn.getErrorStream() : conn.getInputStream()
                        ).useDelimiter("\\A");

                        String responseStr = scanner.hasNext() ? scanner.next() : "";

                        System.out.println("🔵 Daakia: HTTP " + code);
                        System.out.println("🟢 Daakia: Raw response = " + responseStr);

                        Value result;
                        try {
                            result = context.eval("js", "JSON.parse").execute(responseStr);
                        } catch (Exception ex) {
                            result = context.asValue(responseStr);
                        }

                        callback.execute(null, result);
                    }
                    catch (PolyglotException e) {
                        System.err.println("💥 JavaScript execution error:");
                        e.printStackTrace();  // full JS stack trace, even from `await` or callbacks
                        if (e.isGuestException()) {
                            System.err.println("🧠 JS Message: " + e.getMessage());
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        callback.execute(e.getMessage(), null);
                    }
                }
                return null;
            };
        }
        return null;
    }

    static class HttpHeaders extends java.util.HashMap<String, String> {}
}

class DaakiaEnvAccessor extends AbstractDaakiaProxy {

    private final String parentMemberKey;

    public DaakiaEnvAccessor(String parentMemberKey, DataContext dataContext) {
        super(dataContext);
        this.parentMemberKey = parentMemberKey;
    }


    @Override
    public Object getMember(String key) {
        if("globals".equals(parentMemberKey)) {
            return globalGetterSetter(key);
        }
        else if("environment".equals(parentMemberKey)) {
            return envGetterSetter(key);
        }
        return null;
    }

    private ProxyExecutable globalGetterSetter(String key) {
        if ("set".equals(key)) {
            return args -> {
                if (args.length == 2) {
                    setGlobalEnvVariable(args[0].asString(), args[1].asString());
                }
                return null;
            };
        }
        if ("get".equals(key)) {
            return args -> {
                if (args.length == 1) {
                    return getGlobalEnvVariable(args[0].asString());
                }
                return null;
            };
        }
        return null;
    }

    private ProxyExecutable envGetterSetter(String key) {
        if ("set".equals(key)) {
            return args -> {
                if (args.length == 2) {
                    setEnvVariable(args[0].asString(), args[1].asString());
                }
                return null;
            };
        }
        if ("get".equals(key)) {
            return args -> {
                if (args.length == 1) {
                    return getEnvVariable(args[0].asString());
                }
                return null;
            };
        }
        return null;
    }
}
