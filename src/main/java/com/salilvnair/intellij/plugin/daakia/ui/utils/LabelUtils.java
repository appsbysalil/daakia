package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ui.JBColor;
import java.awt.*;

public class LabelUtils {
    private LabelUtils() {}

    public static String coloredText(String prefix, String mainText, String suffix, String hexCode) {
        return "<html>"+(prefix!=null ? prefix : "")+" <strong><font color='"+hexCode+"'>"+mainText+"</font></strong> "+(suffix != null ? suffix : "")+"</html>";
    }

    public static String coloredText(String prefix, String mainText, String suffix, JBColor jbColor) {
        int rgb = jbColor.getRGB();
        String hexCode = String.format("#%06X", rgb & 0xFFFFFF);
        return coloredText(prefix, mainText, suffix, hexCode);
    }

    public static int findComponentIndex(Container container, Component comp) {
        Component[] components = container.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == comp) {
                return i;
            }
        }
        return -1;
    }

    public static String trimLabel(String label, int limit) {
        if(label == null ) {
            return null;
        }

        return label.length() > limit ? label.substring(0, limit) + "..." : label;
    }
}
