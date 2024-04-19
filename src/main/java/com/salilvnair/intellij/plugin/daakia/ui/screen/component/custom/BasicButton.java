package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class BasicButton extends JButton {

    private Icon icon;

    public BasicButton(String name) {
        super(name);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    public BasicButton(Icon icon) {
        super(icon);
        this.icon = icon;
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    public BasicButton(String name, Icon icon) {
        super(name, icon);
        this.icon = icon;
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getModel().isPressed()) {
            g.setColor(new JBColor(new Color(255, 255, 255, 100), new Color(255, 255, 255, 100)));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
