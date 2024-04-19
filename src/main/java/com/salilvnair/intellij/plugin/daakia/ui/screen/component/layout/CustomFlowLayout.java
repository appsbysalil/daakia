package com.salilvnair.intellij.plugin.daakia.ui.screen.component.layout;

import java.awt.*;

public class CustomFlowLayout extends FlowLayout {
    private boolean firstComponent = true;

    public CustomFlowLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            int n = target.getComponentCount();
            if (n == 0) {
                return;
            }

            Insets insets = target.getInsets();
            int maxWidth = target.getWidth() - (insets.left + insets.right + getHgap());
            int x = insets.left - 15;
            int y = insets.top + getVgap(); // Start with vertical gap

            for (int i = 0; i < n; i++) {
                Component c = target.getComponent(i);
                Dimension d = c.getPreferredSize();
                if (!firstComponent) {
                    x += getHgap(); // Add horizontal gap for all components except the first
                }
                else {
                    firstComponent = false; // Set firstComponent to false after processing the first component
                }
                if (x + d.width > maxWidth) {
                    y += d.height + getVgap(); // Add vertical gap for new row
                    x = insets.left; // Reset x position for new row
                }
                c.setBounds(x, y, d.width, d.height);
                x += d.width; // Move x position to the right for the next component
            }
        }
    }
}