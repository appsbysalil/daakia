package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IconButton extends JButton {

    private Icon icon;

    public IconButton(Icon icon) {
        super(icon);
        this.icon = icon;
        setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                setBorderPainted(true);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                setBorderPainted(false);
//            }
//        });
    }

    public IconButton(Icon icon, Dimension preferredSize) {
        super(icon);
        this.icon = icon;
        setPreferredSize(preferredSize);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setContentAreaFilled(false);
            }
        });
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
