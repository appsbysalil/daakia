package com.salilvnair.intellij.plugin.daakia.ui.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Captures System.out and System.err output so that it can be displayed
 * inside the UI when debug mode is enabled.
 */
public class DebugLogManager {
    private static final StringBuilder LOGS = new StringBuilder();

    private static PrintStream originalOut = System.out;
    private static PrintStream originalErr = System.err;
    private static boolean capturing = false;

    public static void startCapture() {
        if (capturing) {
            return;
        }
        capturing = true;
        System.setOut(new PrintStream(new TeeStream(originalOut), true));
        System.setErr(new PrintStream(new TeeStream(originalErr), true));
    }

    public static void stopCapture() {
        if (!capturing) {
            return;
        }
        capturing = false;
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    public static String getLogs() {
        // Normalize captured log line endings based on the OS the plugin is
        // running on. First convert all CRLF/CR to LF and then switch to the
        // system line separator if it differs from LF.
        return LOGS.toString()
                .replace("\r\n", "\n")
                .replace('\r', '\n');
    }

    public static void clear() {
        LOGS.setLength(0);
    }

    private static class TeeStream extends OutputStream {
        private final PrintStream delegate;
        TeeStream(PrintStream delegate) {
            this.delegate = delegate;
        }
        @Override
        public void write(int b) throws IOException {
            LOGS.append((char) b);
            delegate.write(b);
        }
    }
}
