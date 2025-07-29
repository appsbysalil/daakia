package com.salilvnair.intellij.plugin.daakia.ui.utils;

import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.beans.PropertyChangeEvent;
import java.util.Objects;

public class DocumentUtils {
    private DocumentUtils() {}

    public static void addListener(EditorTextField editorTextField, @NotNull DocumentListener listener) {
        editorTextField.addDocumentListener(listener);
    }
}
