package com.salilvnair.intellij.plugin.daakia.ui.core.icon;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;

public class DaakiaIconLoader {

    public static Icon getIcon(String iconPath, Class<?> aClass) {
        return IconLoader.getIcon(iconPath, aClass.getClassLoader());
    }

    public static Icon getIcon(String iconPath, String darkIconPath, Class<?> aClass) {
        boolean isDarkTheme = UIUtil.isUnderDarcula() || (SystemInfo.isMac && UIUtil.isUnderIntelliJLaF());
        if(darkIconPath!=null && isDarkTheme) {
            return IconLoader.getIcon(darkIconPath, aClass.getClassLoader());
        }
        return IconLoader.getIcon(iconPath, aClass.getClassLoader());
    }

}
