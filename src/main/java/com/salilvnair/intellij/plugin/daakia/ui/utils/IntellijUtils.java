package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;

public class IntellijUtils {
    private IntellijUtils() {}

    public static String pluginPath() {
        return PathManager.getPluginsPath();
    }


    public static boolean hasDarkTheme() {
        return !JBColor.isBright() || (SystemInfo.isMac && UIUtil.isUnderIntelliJLaF());
    }

}
