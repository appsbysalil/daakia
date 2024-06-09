package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextInputField extends JTextField {
    private final String placeholder;
    public TextInputField() {
        this(null, 0);
    }
    public TextInputField(String placeholder) {
        this(placeholder, 0);
    }
    public TextInputField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText(""); // Clear placeholder text when field is focused
                    boolean isDarkTheme = UIUtil.isUnderDarcula() || (SystemInfo.isMac && UIUtil.isUnderIntelliJLaF());
                    setForeground(isDarkTheme ? JBColor.BLACK : JBColor.WHITE);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    paintPlaceholderText();
                }
            }
        });
    }

    private void paintPlaceholderText() {
        if (placeholder!=null && getText().isEmpty() && !hasFocus()) {
            setForeground(JBColor.GRAY);
            setText(placeholder);
            setFont(getFont().deriveFont(Font.ITALIC));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholder!=null && getText().isEmpty() && !hasFocus()) {
            g.setColor(JBColor.GRAY);
            g.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int x = insets.left + 5;
            int y = insets.top + fm.getAscent() + 2;
            g.drawString(placeholder, x, y);
        }
    }
}
