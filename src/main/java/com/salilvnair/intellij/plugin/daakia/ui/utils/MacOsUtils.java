package com.salilvnair.intellij.plugin.daakia.ui.utils;


import java.awt.*;

/**
 * @author Salil V Nair
 */
public class MacOsUtils {
    public static void closeOnQuit() {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.setQuitHandler((evt, res) -> {
                System.exit(0);
            });
        }
    }
}
