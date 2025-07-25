package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.type;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DaakiaJavaScriptFileType implements FileType {

    public static final FileType INSTANCE = FileTypeManager.getInstance().getFileTypeByExtension("js");

    @Override
    public @NonNls @NotNull String getName() {
        return "JavaScript";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "js";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public boolean isBinary() {
        return false;
    }
}
