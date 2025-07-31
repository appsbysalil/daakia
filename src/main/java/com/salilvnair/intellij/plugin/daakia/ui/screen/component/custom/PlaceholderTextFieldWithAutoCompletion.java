package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.ui.JBColor;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

public class PlaceholderTextFieldWithAutoCompletion extends TextFieldWithAutoCompletion<String> {
    private final String placeholder;

    public void setText(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                getDocument().setText(text);
            });
        });
    }

    public PlaceholderTextFieldWithAutoCompletion(Project project, String placeholder, List<String> variants, boolean autoComplete, String initialText) {
        super(project, new DaakiaTextFieldCompletionProvider(variants), true, initialText);
        this.placeholder = placeholder;
        setText(initialText == null ? placeholder : initialText);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    Editor editor = getEditor();
                    if(editor != null) {
                        editor.getColorsScheme().setColor(EditorColors.CARET_ROW_COLOR, JBColor.BLACK);
                        editor.getContentComponent().setForeground(JBColor.BLACK);
                    }
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

    private static class DaakiaTextFieldCompletionProvider extends TextFieldWithAutoCompletionListProvider<String> {
        public DaakiaTextFieldCompletionProvider(List<String> variants) {
            super(variants);
        }

        @Override
        protected @NotNull String getLookupString(@NotNull String item) {
            return item;
        }
    }

    private void paintPlaceholderText() {
        if (placeholder!=null && getText().isEmpty() && !hasFocus()) {
            setText(placeholder);
            setFont(getFont().deriveFont(Font.ITALIC));
            SwingUtilities.invokeLater(() -> {
                Editor editor = getEditor();
                if(editor != null) {
                    editor.getColorsScheme().setColor(EditorColors.CARET_ROW_COLOR, JBColor.GRAY);
                    editor.getContentComponent().setForeground(JBColor.GRAY);
                }
            });
        }
    }
}
