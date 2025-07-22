package com.salilvnair.intellij.plugin.daakia.script.main;

import com.salilvnair.intellij.plugin.daakia.script.bridge.DaakiaBridge;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Variable;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * DaakiaScriptExecutor - Entry point to execute user-defined JS scripts using pm.* API
 */
public class DaakiaScriptExecutor implements AutoCloseable {
    private final DaakiaBridge bridge;
    private final Context context;

    public static DaakiaScriptExecutor init(DataContext dataContext) {
        return new DaakiaScriptExecutor(dataContext);
    }

    public DaakiaScriptExecutor(DataContext dataContext) {
//        this.context = Context.newBuilder("js")
//                .allowAllAccess(true)
//                .build();
        this.context = Context.newBuilder("js")
                .allowAllAccess(true)
                .allowAllAccess(true)
                .allowCreateThread(true)
                .out(System.out)
                .err(System.err)
                .option("js.ecmascript-version", "2022") // Needed for top-level await
                .option("js.commonjs-require", "true")   // Optional if using CommonJS
                .option("js.esm-eval-returns-exports", "true") // Ensure result from module
                .option("js.syntax-extensions", "true")  // Just in case of relaxed syntax
                .build();
        this.bridge = new DaakiaBridge(context, dataContext);
        context.getBindings("js").putMember("pm", bridge.getMember("pm"));
    }

    public void executeScript(String script) {
//        context.eval("js", script);
        try {
            Source source = Source.newBuilder("js", script, "user-script.mjs")
                    .mimeType("application/javascript+module")
                    .build();
            context.eval(source);
        }
        catch (PolyglotException e) {
            System.err.println("💥 JavaScript execution error:");
            e.printStackTrace();  // full JS stack trace, even from `await` or callbacks
            if (e.isGuestException()) {
                System.err.println("🧠 JS Message: " + e.getMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeScriptFile(String path) throws IOException {
        String script = Files.readString(Paths.get(path));
        executeScript(script);
    }

    public Environment getEnvironment() {
        return bridge.getEnvironment();
    }

    public String getVariableValue(String key) {
        for (Variable v : bridge.getEnvironment().variables()) {
            if (key.equals(v.getKey())) {
                return v.getCurrentValue();
            }
        }
        return null;
    }

    @Override
    public void close() {
        context.close(true);
    }
}
