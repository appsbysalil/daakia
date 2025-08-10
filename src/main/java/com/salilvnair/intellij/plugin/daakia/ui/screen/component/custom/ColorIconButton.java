package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.ui.JBColor;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.ImageUtil;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorIconButton extends JButton {

    private final Icon baseIcon;        // e.g., AllIcons.Actions.Close (SVG/cached)
    private JBColor baseTint;           // normal-state tint
    private final int logicalSizePx;    // desired logical size (not DPI-scaled), e.g., 24, 32

    public ColorIconButton(Icon icon) {
        this(icon, JBColor.RED, icon.getIconWidth() > 0 ? icon.getIconWidth() : 16);
    }

    public ColorIconButton(Icon icon, JBColor tint, int logicalSizePx) {
        super();
        this.baseIcon = icon;
        this.baseTint = tint;
        this.logicalSizePx = logicalSizePx;

        // Size button in logical px; JBUI.scale applies device DPI
        Dimension d = new Dimension(JBUI.scale(logicalSizePx), JBUI.scale(logicalSizePx));
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);

        setBorder(BorderFactory.createEmptyBorder());
        setMargin(JBUI.emptyInsets());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
    }

    public void setTint(JBColor tint) {
        this.baseTint = tint;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (baseIcon == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int baseW = Math.max(1, baseIcon.getIconWidth());
            float factor = (float) logicalSizePx / baseW;

            // 1) DPI-aware vector scaling by the platform (crisp on Windows HiDPI)
            Icon dpiIcon = IconUtil.scale(baseIcon, this, factor);

            // 2) Convert to image WITHOUT further scaling
            Image baseImg = IconUtil.toImage(dpiIcon);
            int w = baseImg.getWidth(null);
            int h = baseImg.getHeight(null);

            // 3) Tint in-place using SrcIn (keeps icon shape)
            BufferedImage tinted = ImageUtil.createImage(g2, w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tg = tinted.createGraphics();
            try {
                setupHints(tg);
                tg.drawImage(baseImg, 0, 0, null);  // already DPI-correct
                tg.setComposite(AlphaComposite.SrcIn);
                tg.setColor(baseTint);
                tg.fillRect(0, 0, w, h);

                // Hover/pressed overlay using the same alpha (no rectangles)
                if (getModel().isPressed() || getModel().isRollover()) {
                    Color overlay = getModel().isPressed()
                            ? new JBColor(new Color(255, 0, 0, 110), new Color(255, 0, 0, 110))
                            : new JBColor(new Color(255, 0, 0, 70),  new Color(255, 0, 0, 70));
                    tg.setComposite(AlphaComposite.SrcOver);
                    tg.setColor(overlay);
                    tg.fillRect(0, 0, w, h);
                }
            } finally {
                tg.dispose();
            }

            // 4) Center draw (no scaling here)
            int x = (getWidth()  - w) / 2;
            int y = (getHeight() - h) / 2;
            g2.drawImage(tinted, x, y, null);

        } finally {
            g2.dispose();
        }
    }

    private static void setupHints(Graphics2D g2) {
        // We are not resampling here, but keep quality hints anyway
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }
}
