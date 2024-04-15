package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBPanel;

import java.awt.*;

public class Spacer extends JBPanel<Spacer> {

    public Spacer() {
        setPreferredSize(new Dimension(10, 0));
    }

    public Spacer(Dimension dimension) {
        setPreferredSize(dimension);
    }

    public Spacer(int width, int height) {
        setPreferredSize(new Dimension(width, height));
    }

    public Spacer(int width) {
        setPreferredSize(new Dimension(width, 0));
    }


    public static Spacer addSpacer() {
        return new Spacer();
    }

    public static Spacer addSpacer(Dimension dimension) {
        return new Spacer(dimension);
    }

    public static Spacer addSpacer(int width, int height) {
        return new Spacer(width, height);
    }

    public static Spacer addSpacer(int width) {
        return new Spacer(width);
    }
}
