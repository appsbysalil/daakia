package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.openapi.application.PathManager;

public class IntellijUtils {
    private IntellijUtils() {}

    public static String pluginPath() {
        return PathManager.getPluginsPath();
    }
}
