package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor;

import com.intellij.find.EditorSearchSession;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.type.DaakiaJavaScriptFileType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Custom editor supporting syntax highlighting, folding, gutter, and inline error highlighting.
 */
public class DaakiaEditorX extends JBPanel<DaakiaEditorX> {
    private EditorEx editor;
    private FileType fileType;
    private final Project project;
    private final boolean viewOnly;

    public DaakiaEditorX(FileType fileType, Project project) {
        this(fileType, project, false);
    }

    public DaakiaEditorX(FileType fileType, Project project, boolean viewOnly) {
        super(new BorderLayout());
        this.viewOnly = viewOnly;
        this.fileType = fileType != null ? fileType : PlainTextFileType.INSTANCE;
        this.project = project;
        createEditor("");
    }

    private void createEditor(String initialText) {
        switch (fileType) {
            case JsonFileType jsonFileType -> createJsonEditor(initialText);
            case DaakiaJavaScriptFileType daakiaJavaScriptFileType -> createJsEditor(initialText);
            case XmlFileType xmlFileType -> createXmlEditor(initialText);
            case null, default -> createTextEditor(initialText);
        }
    }

    private void createXmlEditor(String initialText) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            FileType xmlFileType = XmlFileType.INSTANCE;
            LightVirtualFile virtualFile = new LightVirtualFile("dummy.xml", xmlFileType, initialText);

            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) throw new IllegalStateException("XML PSI file is null");

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) throw new IllegalStateException("XML Document is null");

            editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, xmlFileType, false);

            editor.getSettings().setLineNumbersShown(true);
            editor.getSettings().setFoldingOutlineShown(true);
            editor.getSettings().setLineMarkerAreaShown(true);
            editor.getSettings().setAutoCodeFoldingEnabled(true);
            editor.getSettings().setIndentGuidesShown(true);
            editor.getSettings().setGutterIconsShown(true);
            editor.getSettings().setAdditionalPageAtBottom(true);
            editor.getSettings().setUseTabCharacter(false);
            editor.setViewer(viewOnly);

            EditorHighlighter highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(xmlFileType, editor.getColorsScheme(), project);
            editor.setHighlighter(highlighter);

            add(editor.getComponent(), BorderLayout.CENTER);

            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    highlightSyntaxErrors(); // Optional: you may skip error wave for XML
                }
            });

            highlightSyntaxErrors();
        });
    }

    private void createHtmlEditor(String initialText) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            FileType htmlFileType = HtmlFileType.INSTANCE;
            LightVirtualFile virtualFile = new LightVirtualFile("dummy.html", htmlFileType, initialText);

            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) throw new IllegalStateException("XML PSI file is null");

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) throw new IllegalStateException("XML Document is null");

            editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, htmlFileType, false);

            editor.getSettings().setLineNumbersShown(true);
            editor.getSettings().setFoldingOutlineShown(true);
            editor.getSettings().setLineMarkerAreaShown(true);
            editor.getSettings().setAutoCodeFoldingEnabled(true);
            editor.getSettings().setIndentGuidesShown(true);
            editor.getSettings().setGutterIconsShown(true);
            editor.getSettings().setAdditionalPageAtBottom(true);
            editor.getSettings().setUseTabCharacter(false);
            editor.setViewer(viewOnly);

            EditorHighlighter highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(htmlFileType, editor.getColorsScheme(), project);
            editor.setHighlighter(highlighter);

            add(editor.getComponent(), BorderLayout.CENTER);

            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    highlightSyntaxErrors(); // Optional: you may skip error wave for XML
                }
            });

            highlightSyntaxErrors();
        });
    }


    private void createJsonEditor(String initialText) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            FileType jsonFileType = JsonFileType.INSTANCE;
            LightVirtualFile virtualFile = new LightVirtualFile("dummy.json", jsonFileType, initialText);

            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) throw new IllegalStateException("PSI file is null");

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) throw new IllegalStateException("Document from PSI file is null");

            editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, jsonFileType, false);

            editor.getSettings().setLineNumbersShown(true);
            editor.getSettings().setFoldingOutlineShown(true);
            editor.getSettings().setLineMarkerAreaShown(true);
            editor.getSettings().setAutoCodeFoldingEnabled(true);
            editor.getSettings().setIndentGuidesShown(true);
            editor.getSettings().setGutterIconsShown(true);
            editor.getSettings().setAdditionalPageAtBottom(true);
            editor.getSettings().setUseTabCharacter(false);
            editor.setViewer(viewOnly);

            EditorHighlighter highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(jsonFileType, editor.getColorsScheme(), project);
            editor.setHighlighter(highlighter);

            add(editor.getComponent(), BorderLayout.CENTER);

            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    highlightSyntaxErrors();
                }
            });
            highlightSyntaxErrors();
        });
    }

    private void createJsEditor(String initialText) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            FileType jsFileType = DaakiaJavaScriptFileType.INSTANCE;
            LightVirtualFile virtualFile = new LightVirtualFile("dummy.js", jsFileType, initialText);
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) throw new IllegalStateException("JS PSI file is null");

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) throw new IllegalStateException("Document from PSI file is null");

            editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, jsFileType, false);

            editor.getSettings().setLineNumbersShown(true);
            editor.getSettings().setFoldingOutlineShown(true);
            editor.getSettings().setLineMarkerAreaShown(true);
            editor.getSettings().setAutoCodeFoldingEnabled(true);
            editor.getSettings().setIndentGuidesShown(true);
            editor.getSettings().setGutterIconsShown(true);
            editor.getSettings().setAdditionalPageAtBottom(true);
            editor.getSettings().setUseTabCharacter(false);
            editor.setViewer(viewOnly);

            EditorHighlighter highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(jsFileType, editor.getColorsScheme(), project);
            editor.setHighlighter(highlighter);

            add(editor.getComponent(), BorderLayout.CENTER);

            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    highlightSyntaxErrors();
                }
            });
            highlightSyntaxErrors();
        });
    }




    private void createTextEditor(String initialText) {
        FileType textFileType = fileType != null ? fileType : PlainTextFileType.INSTANCE;
        ApplicationManager.getApplication().runWriteAction(() -> {
            LightVirtualFile virtualFile = new LightVirtualFile("dummy.txt", textFileType, initialText);

            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) throw new IllegalStateException("PSI file is null");

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) throw new IllegalStateException("Document from PSI file is null");

            editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, textFileType, false);

            editor.getSettings().setLineNumbersShown(true);
            editor.getSettings().setFoldingOutlineShown(true);
            editor.getSettings().setLineMarkerAreaShown(true);
            editor.getSettings().setAutoCodeFoldingEnabled(true);
            editor.getSettings().setIndentGuidesShown(true);
            editor.getSettings().setGutterIconsShown(true);
            editor.getSettings().setAdditionalPageAtBottom(true);
            editor.getSettings().setUseTabCharacter(false);
            editor.setViewer(viewOnly);

            EditorHighlighter highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(textFileType, editor.getColorsScheme(), project);
            editor.setHighlighter(highlighter);

            add(editor.getComponent(), BorderLayout.CENTER);

            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    highlightSyntaxErrors();
                }
            });

            highlightSyntaxErrors();
        });
    }


    public EditorEx editor() {
        return editor;
    }

    public String text() {
        return editor.getDocument().getText();
    }
    public String getText() {
        return text();
    }

    public void setText(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                editor.getDocument().setText(text == null ? "" : text);
            });
        });
    }

    public void updateFileType(FileType newFileType) {
        if (newFileType == null) newFileType = PlainTextFileType.INSTANCE;
        if (newFileType.equals(this.fileType)) return;

        String text = text();
        EditorFactory.getInstance().releaseEditor(editor);
        remove(editor.getComponent());
        this.fileType = newFileType;
        createEditor("");
        setText(text);
        revalidate();
        repaint();
    }

    private void highlightSyntaxErrors() {
        editor.getMarkupModel().removeAllHighlighters();
        if (fileType instanceof JsonFileType) {
            highlightJsonErrors();
        }
    }

    private void highlightJsonErrors() {
        String text = text();
        try {
            new org.json.JSONTokener(text).nextValue();
        } catch (org.json.JSONException e) {
            int line = 1;
            try {
                String msg = e.getMessage();
                if (msg != null && msg.contains("line")) {
                    line = Integer.parseInt(msg.substring(msg.indexOf("line") + 5,
                            msg.indexOf(']', msg.indexOf("line"))));
                }
            } catch (Exception ignore) {}
            int start = editor.getDocument().getLineStartOffset(Math.max(line - 1, 0));
            int end = editor.getDocument().getLineEndOffset(Math.max(line - 1, 0));
            TextAttributes attrs = new TextAttributes();
            attrs.setEffectType(EffectType.WAVE_UNDERSCORE);
            attrs.setEffectColor(JBColor.RED);
            editor.getMarkupModel().addRangeHighlighter(start, end, HighlighterLayer.ERROR + 1, attrs, HighlighterTargetArea.EXACT_RANGE);
        }
    }

    // ✅ Optional: Trigger search inline (call from outside with shortcut or button)
    public void showSearch() {
        EditorSearchSession.start(editor, project);
    }
}
