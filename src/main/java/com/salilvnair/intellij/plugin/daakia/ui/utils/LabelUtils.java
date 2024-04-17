package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ui.JBColor;

public class LabelUtils {
    private LabelUtils() {}

    public static String colorText(String prefix, String mainText, String suffix, String hexCode) {
        return "<html>"+(prefix!=null ? prefix : "")+"<strong><font color='"+hexCode+"'>"+mainText+"</font></strong>"+(suffix != null ? suffix : "")+"</html>";
    }

    public static String colorText(String prefix, String mainText, String suffix, JBColor jbColor) {
        // Get the RGB value
        int rgb = jbColor.getRGB();

        // Convert RGB to hexadecimal
        String hexCode = String.format("#%06X", rgb & 0xFFFFFF);

        return colorText(prefix, mainText, suffix, hexCode);
    }
}
