package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorIconButton extends JButton {

    private final Icon baseIcon;
    private final JBColor iconColor = new JBColor(new Color(255, 0, 0), new Color(255, 0, 0)); // Red for both themes

    public ColorIconButton(Icon icon) {
        this(icon, new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }

    public ColorIconButton(Icon icon, Dimension preferredSize) {
        super();
        this.baseIcon = icon;
        setPreferredSize(preferredSize);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (baseIcon != null) {
            int x = (getWidth() - baseIcon.getIconWidth()) / 2;
            int y = (getHeight() - baseIcon.getIconHeight()) / 2;
            paintTintedIcon((Graphics2D) g, baseIcon, iconColor, x, y);
        }

        if (getModel().isPressed()) {
            g.setColor(new JBColor(new Color(255, 0, 0, 60), new Color(255, 0, 0, 60))); // Red overlay on press
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static void paintTintedIcon(Graphics2D g2, Icon icon, JBColor tint, int x, int y) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = img.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        icon.paintIcon(null, ig, 0, 0);
        ig.setComposite(AlphaComposite.SrcIn);
        ig.setColor(tint);
        ig.fillRect(0, 0, w, h);
        ig.dispose();

        g2.drawImage(img, x, y, null);
    }
}
