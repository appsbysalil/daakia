package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.ui.JBColor;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * @author Salil V Nair
 */
public class IconUtils {
    private IconUtils() {}

    public static ImageIcon createIcon(String path) {
        URL url = IconUtils.class.getResource(path);
        if(url == null ) {
            System.err.println("icon not found");
        }
        assert url != null;
        return new ImageIcon(url);
    }

    public static ImageIcon createIcon(Icon icon) {
        return new ImageIcon(IconUtil.toBufferedImage(icon));
    }

    public static ImageIcon createIconByTheme(String... paths) {
        String lightPath = paths[0];
        String darkPath = paths[1];
        if(!JBColor.isBright()) {
            return createIcon(darkPath);
        }
        return createIcon(lightPath);
    }

    public static ImageIcon createIconByTheme(int w , int h, String... paths) {
        String lightPath = paths[0];
        String darkPath = paths[1];
        if(!JBColor.isBright()) {
            return createIcon(darkPath, w, h);
        }
        return createIcon(lightPath, w, h);
    }

    public static ImageIcon createIcon(String path, int w , int h) {
        return scaleImageIcon(createIcon(path), w, h);
    }

    public static ImageIcon scaleImageIcon(ImageIcon srcImg, int w, int h){
        return new ImageIcon(srcImg.getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT));
    }

    public static ImageIcon scaleImageIcon(Image srcImg, int w, int h){
        BufferedImage resizedImg = ImageUtil.createImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }
}
