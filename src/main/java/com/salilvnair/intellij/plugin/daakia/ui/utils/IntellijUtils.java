package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo;
import com.intellij.ide.ui.laf.darcula.DarculaLaf;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.NewUI;
import com.intellij.util.ui.StartupUiUtil;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;

public class IntellijUtils {
    private IntellijUtils() {}

    public static String pluginPath() {
        return PathManager.getPluginsPath();
    }


    public static boolean hasDarkTheme() {
        return !JBColor.isBright() || (SystemInfo.isMac && UIUtil.isUnderIntelliJLaF());
    }

    public static boolean newUiDarkTheme() {
        return NewUI.isEnabled() ? StartupUiUtil.INSTANCE.isDarkTheme() : hasDarkTheme();
    }

}
