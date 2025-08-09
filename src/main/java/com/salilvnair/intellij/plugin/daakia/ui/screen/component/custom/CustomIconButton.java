package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CustomIconButton extends JButton {

    private final Icon baseIcon;

    // Background colors
    private JBColor bgInitialColor;
    private JBColor bgPressedColor;
    private JBColor bgHoverColor;

    // Icon tint colors
    private JBColor iconInitialColor;
    private JBColor iconPressedColor;
    private JBColor iconHoverColor;

    private boolean hovering = false;

    // --- Defaults ---
    private static final JBColor DEF_BG_INITIAL   = new JBColor(new Color(255, 255, 255, 100),  new Color(255, 255, 255, 100));
    private static final JBColor DEF_BG_PRESSED   = new JBColor(new Color(255, 255, 255, 100), new Color(255, 255, 255, 100));
    private static final JBColor DEF_BG_HOVER     = new JBColor(new Color(255, 255, 255, 100),  new Color(255, 255, 255, 100));

    private static final JBColor DEF_ICON_INITIAL = new JBColor(new Color(255, 255, 255, 100),  new Color(255, 255, 255, 100));
    private static final JBColor DEF_ICON_PRESSED = new JBColor(new Color(255, 255, 255, 100),  new Color(255, 255, 255, 100));
    private static final JBColor DEF_ICON_HOVER   = new JBColor(new Color(255, 255, 255, 100),  new Color(255, 255, 255, 100));

    // --- Constructors ---

    // 0️⃣ Just icon (defaults)
    public CustomIconButton(Icon icon) {
        this(icon, new Dimension(icon.getIconWidth(), icon.getIconHeight()),
                DEF_BG_INITIAL, DEF_BG_PRESSED, DEF_BG_HOVER,
                DEF_ICON_INITIAL, DEF_ICON_PRESSED, DEF_ICON_HOVER);
    }

    public CustomIconButton(Icon icon, Dimension dimension) {
        this(icon, dimension,
                DEF_BG_INITIAL, DEF_BG_PRESSED, DEF_BG_HOVER,
                DEF_ICON_INITIAL, DEF_ICON_PRESSED, DEF_ICON_HOVER);
    }

    // 1️⃣ Dimension + icon colors only (background defaults)
    public CustomIconButton(Dimension preferredSize,
                            Icon icon,
                            JBColor initialIconColor,
                            JBColor pressedIconColor,
                            JBColor hoverIconColor) {
        this(icon, preferredSize,
                DEF_BG_INITIAL, DEF_BG_PRESSED, DEF_BG_HOVER,
                initialIconColor, pressedIconColor, hoverIconColor);
    }

    public CustomIconButton(Dimension preferredSize,
                            Icon icon,
                            JBColor initialIconColor) {
        this(icon, preferredSize,
                DEF_BG_INITIAL, DEF_BG_PRESSED, DEF_BG_HOVER,
                initialIconColor, DEF_ICON_PRESSED, DEF_ICON_HOVER);
    }

    // 2️⃣ Dimension + background colors only (icon defaults)
    public CustomIconButton(Icon icon,
                            Dimension preferredSize,
                            JBColor initialBgColor,
                            JBColor pressedBgColor,
                            JBColor hoverBgColor) {
        this(icon, preferredSize,
                initialBgColor, pressedBgColor, hoverBgColor,
                DEF_ICON_INITIAL, DEF_ICON_PRESSED, DEF_ICON_HOVER);
    }

    public CustomIconButton(Icon icon,
                            Dimension preferredSize,
                            JBColor initialBgColor) {
        this(icon, preferredSize,
                initialBgColor, DEF_BG_PRESSED, DEF_BG_HOVER,
                DEF_ICON_INITIAL, DEF_ICON_PRESSED, DEF_ICON_HOVER);
    }

    // 3️⃣ Dimension + both bg + icon colors
    public CustomIconButton(Icon icon,
                            Dimension preferredSize,
                            JBColor initialBg, JBColor pressedBg, JBColor hoverBg,
                            JBColor initialIcon, JBColor pressedIcon, JBColor hoverIcon) {
        super();
        this.baseIcon = icon;

        this.bgInitialColor   = initialBg;
        this.bgPressedColor   = pressedBg;
        this.bgHoverColor     = hoverBg;

        this.iconInitialColor = initialIcon;
        this.iconPressedColor = pressedIcon;
        this.iconHoverColor   = hoverIcon;

        initCommon(preferredSize);
    }


    public CustomIconButton(Icon icon,
                            Dimension preferredSize,
                            JBColor initialBg,
                            JBColor initialIcon) {
        super();
        this.baseIcon = icon;

        this.bgInitialColor   = initialBg;
        this.bgPressedColor   = DEF_BG_PRESSED;
        this.bgHoverColor     = DEF_BG_HOVER;

        this.iconInitialColor = initialIcon;
        this.iconPressedColor = DEF_ICON_PRESSED;
        this.iconHoverColor   = DEF_ICON_HOVER;

        initCommon(preferredSize);
    }
    // --- Init ---
    private void initCommon(Dimension preferredSize) {
        setPreferredSize(preferredSize);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovering = false; repaint(); }
        });
    }

    // --- Painting ---
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 1) Background
        g2.setColor(bgInitialColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (getModel().isPressed() && isEnabled()) {
            g2.setColor(bgPressedColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else if (hovering && isEnabled()) {
            g2.setColor(bgHoverColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2) Icon (tinted)
        if (baseIcon != null) {
            JBColor tint = iconInitialColor;
            if (getModel().isPressed() && isEnabled()) {
                tint = iconPressedColor;
            } else if (hovering && isEnabled()) {
                tint = iconHoverColor;
            }

            int x = (getWidth() - baseIcon.getIconWidth()) / 2;
            int y = (getHeight() - baseIcon.getIconHeight()) / 2;
            paintTintedIcon(g2, baseIcon, tint, x, y);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private static void paintTintedIcon(Graphics2D g2, Icon icon, JBColor tint, int x, int y) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        BufferedImage img = ImageUtil.createImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = img.createGraphics();
        ig.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        icon.paintIcon(null, ig, 0, 0);
        ig.setComposite(AlphaComposite.SrcIn);
        ig.setColor(tint);
        ig.fillRect(0, 0, w, h);
        ig.dispose();

        g2.drawImage(img, x, y, null);
    }

    // --- Setters (BG) ---
    public void setInitialColor(JBColor c) { this.bgInitialColor = c; repaint(); }
    public void setPressedColor(JBColor c) { this.bgPressedColor = c; repaint(); }
    public void setHoverColor(JBColor c)   { this.bgHoverColor   = c; repaint(); }

    // --- Setters (Icon tints) ---
    public void setIconInitialColor(JBColor c) { this.iconInitialColor = c; repaint(); }
    public void setIconPressedColor(JBColor c) { this.iconPressedColor = c; repaint(); }
    public void setIconHoverColor(JBColor c)   { this.iconHoverColor   = c; repaint(); }
}

