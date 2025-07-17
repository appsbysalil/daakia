package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PasswordInputField extends JPasswordField {
    private final String placeholder;

    public PasswordInputField() {
        this(null, 0);
    }

    public PasswordInputField(String placeholder) {
        this(placeholder, 0);
    }

    public PasswordInputField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        paintPlaceholderText();
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(getPassword()).equals(placeholder)) {
                    setText("");
                    setEchoChar('\u2022');
                    setForeground(JBColor.BLACK);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(getPassword()).isEmpty()) {
                    paintPlaceholderText();
                }
            }
        });
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        if(t != null && !t.isEmpty() && !t.equals(placeholder)) {
            setForeground(JBColor.BLACK);
            setFont(getFont().deriveFont(Font.PLAIN));
        } else if(t == null || t.isEmpty()) {
            paintPlaceholderText();
        }
    }

    private void paintPlaceholderText() {
        if (placeholder != null && String.valueOf(getPassword()).isEmpty() && !hasFocus()) {
            setForeground(JBColor.GRAY);
            setText(placeholder);
            setFont(getFont().deriveFont(Font.ITALIC));
            setEchoChar((char) 0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholder != null && String.valueOf(getPassword()).isEmpty() && !hasFocus()) {
            g.setColor(JBColor.GRAY);
            g.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int x = insets.left + 5;
            int y = insets.top + fm.getAscent() + 2;
            g.drawString(placeholder, x, y);
        }
    }

    public String placeholder() {
        return placeholder;
    }

    public boolean containsText() {
        return !String.valueOf(getPassword()).equals(placeholder);
    }
}
