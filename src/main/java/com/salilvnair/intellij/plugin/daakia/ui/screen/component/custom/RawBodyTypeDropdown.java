package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class RawBodyTypeDropdown extends JPanel {
    public enum RawType {
        JSON("JSON"),
        XML("XML"),
        HTML("HTML"),
        TEXT("TEXT"),
        ;

        private final String label;

        RawType(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final ComboBox<RawType> dropdown;

    public RawBodyTypeDropdown() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(JBUI.Borders.empty(5));

        List<RawType> items = Arrays.asList(RawType.values());

        dropdown = new ComboBox<>(items.toArray(new RawType[0]));
        dropdown.setPreferredSize(new Dimension(100, 28));
        dropdown.setFocusable(false);
        dropdown.setOpaque(false);

        // Postman-style visual tweaks
        dropdown.setBackground(JBColor.PanelBackground);
        dropdown.setRenderer(new SimpleListCellRenderer<>() {
            @Override
            public void customize(JList<? extends RawType> list, RawType value, int index, boolean selected, boolean hasFocus) {
                setText(value.toString());
                setForeground(selected ? JBColor.foreground() : JBColor.GRAY);
                setBackground(selected ? JBColor.LIGHT_GRAY : JBColor.PanelBackground);
            }
        });

        add(dropdown, BorderLayout.CENTER);
    }

    public RawType getSelectedType() {
        return (RawType) dropdown.getSelectedItem();
    }

    public void setSelectedType(RawType type) {
        dropdown.setSelectedItem(type);
    }

    public void addSelectionListener(Runnable listener) {
        dropdown.addActionListener(e -> listener.run());
    }
}
