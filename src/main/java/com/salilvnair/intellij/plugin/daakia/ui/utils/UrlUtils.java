package com.salilvnair.intellij.plugin.daakia.ui.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
    private UrlUtils() {}

    public static boolean validateURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            // Check if protocol is HTTP or HTTPS
            if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
                return false;
            }
            // Check if URL has a host component
            return url.getHost() != null && !url.getHost().isEmpty();
        }
        catch (MalformedURLException e) {
            return false;
        }
    }
}
