package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor;

import com.intellij.find.EditorSearchSession;
import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.EditorPopupHandler;
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
import com.salilvnair.intellij.plugin.daakia.ui.utils.FormatUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom editor supporting syntax highlighting, folding, gutter, and inline error highlighting.
 */
public class DaakiaEditorX extends JBPanel<DaakiaEditorX> {
    private EditorEx editor;
    private FileType fileType;
    private final Project project;
    private final boolean viewOnly;
    private boolean logView;

    private final Set<String> allowedPopupMenuIds = Set.of(
            "EditorPopupMenu1"
    );

    private final Set<String> allowedPopupMenuGroupIds = Set.of(
            "FoldingGroup"
    );

    private final Set<String> allowedFoldingActionIds = Set.of(
            "ExpandAllRegions", "CollapseAllRegions",
            "EditorFold", "EditorUnfold"
    );

    private final Set<String> defaultContextMenuOptions = Set.of(
            "$Cut", "$Copy", "$Paste"
    );

    private final Set<String> logViewContextMenuOptions = Set.of(
            "$Copy"
    );

    private final Set<String> languageContextMenuOptions = Set.of(
            "CompareClipboardWithSelection", "Copy.Paste.Special", "JsonCopyPointer"
    );

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

    public DaakiaEditorX(Project project, boolean logView) {
        super(new BorderLayout());
        this.viewOnly = true;
        this.logView = logView;
        this.fileType = PlainTextFileType.INSTANCE;
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
        attachContextMenu();
    }

    private void createXmlEditor(String initialText) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            FileType xmlFileType = XmlFileType.INSTANCE;
            LightVirtualFile virtualFile = new LightVirtualFile("response.xml", xmlFileType, initialText);

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
            LightVirtualFile virtualFile = new LightVirtualFile("response.html", htmlFileType, initialText);

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
            LightVirtualFile virtualFile = new LightVirtualFile("response.json", jsonFileType, initialText);
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile == null) throw new IllegalStateException("PSI file is null");
            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            if (document == null) throw new IllegalStateException("Document from PSI file is null");

            editor = (EditorEx) EditorFactory.getInstance().createEditor(document, project, jsonFileType, false);
            editor.installPopupHandler(EditorPopupHandler.NONE);

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
            LightVirtualFile virtualFile = new LightVirtualFile("response.js", jsFileType, initialText);
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
            LightVirtualFile virtualFile = new LightVirtualFile("response.txt", textFileType, initialText);

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
        String sanitized = com.intellij.openapi.util.text.StringUtil.convertLineSeparators(text == null ? "" : text);
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                editor.getDocument().setText(sanitized);
            });
        });
    }

    public void setText(String text, FileType fileType) {
        String sanitized = com.intellij.openapi.util.text.StringUtil.convertLineSeparators(text == null ? "" : text);
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                editor.getDocument().setText(sanitized);
            });
        });
        updateFileType(fileType);
    }

    public void updateFileType(FileType newFileType) {
        if (newFileType == null) newFileType = PlainTextFileType.INSTANCE;
        if (newFileType.equals(this.fileType)) return;
        FileType finalNewFileType = newFileType;
        ApplicationManager.getApplication().invokeLater(() -> {
            String text = text();
            EditorFactory.getInstance().releaseEditor(editor);
            remove(editor.getComponent());
            this.fileType = finalNewFileType;
            createEditor("");
            setText(text);
            revalidate();
            repaint();
        });
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

    private void attachContextMenu() {
        DefaultActionGroup actionGroup = createPopupMenuActions();
        editor.getContentComponent().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    ActionPopupMenu popupMenu = ActionManager.getInstance()
                            .createActionPopupMenu(ActionPlaces.EDITOR_POPUP, actionGroup);
                    popupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private DefaultActionGroup createPopupMenuActions() {
        return createDefaultActionGroup();
    }

    private DefaultActionGroup createDefaultActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        if(logView) {
            actionGroup = filterActionGroup(logViewContextMenuOptions, actionGroup);
            createLogViewActionGroup(actionGroup);
        }
        else {
            actionGroup = filterActionGroup(defaultContextMenuOptions, actionGroup);
            createEditorViewActionGroup(actionGroup);
        }
        return actionGroup;
    }

    private void createEditorViewActionGroup(DefaultActionGroup actionGroup) {
        actionGroup = filterActionGroup(languageContextMenuOptions, actionGroup);
        for (String groupId : allowedPopupMenuIds) {
            AnAction groupAction = ActionManager.getInstance().getAction(groupId);
            if (groupAction instanceof DefaultActionGroup group) {
                createAllowedPopupMenuActionGroup(group, actionGroup);
            }
        }

        // Add formatter button dynamically based on fileType
        DefaultActionGroup finalActionGroup = actionGroup;
        FormatUtils.formatterMap.forEach((ftClass, meta) -> {
            if (ftClass.isInstance(fileType)) {
                finalActionGroup.addSeparator();
                finalActionGroup.add(new AnAction("Format", null, meta.icon()) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        String text = editor.getDocument().getText();
                        try {
                            String formatted = meta.formatter().apply(text);
                            DaakiaEditorX.this.setText(formatted);
                        } catch (Exception ex) {
                            System.out.println("Format Error: " + ex.getMessage());
                        }
                    }
                });
            }
        });
    }


    private void createLogViewActionGroup(DefaultActionGroup actionGroup) {
        actionGroup.addSeparator();
        AnAction anAction = new AnAction("Clear All", null, AllIcons.General.Delete) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                DaakiaEditorX.this.setText("");
            }
            @Override
            public void update(@NotNull AnActionEvent e) {
                String text = editor.getDocument().getText();
                e.getPresentation().setEnabled(!text.trim().isEmpty());
            }
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }
        };
        actionGroup.add(anAction);
    }

    private void createAllowedPopupMenuActionGroup(AnAction action, DefaultActionGroup defaultActionGroup) {
        ActionManager actionManager = ActionManager.getInstance();
        Set<AnAction> alreadyAdded = new HashSet<>();

        if (action instanceof DefaultActionGroup group) {
            for (AnAction child : group.getChildren(actionManager)) {
                String id = actionManager.getId(child);
                if (id != null && allowedPopupMenuGroupIds.contains(id)) {
                    if (alreadyAdded.add(child) && !defaultActionGroup.containsAction(child)) {
                        defaultActionGroup.add(child);
                    }
                }
                createAllowedPopupMenuActionGroup(child, defaultActionGroup);
            }
        } else {
            String id = actionManager.getId(action);
            if (id != null && allowedFoldingActionIds.contains(id)) {
                alreadyAdded.add(action);
                if (!defaultActionGroup.containsAction(action)) {
                    defaultActionGroup.add(action);
                }
            }
        }
    }



    private DefaultActionGroup filterActionGroup(Set<String> allowedMenuOptions, DefaultActionGroup existingActionGroup) {
        AnAction popupAction = ActionManager.getInstance().getAction(IdeActions.GROUP_EDITOR_POPUP);
        DefaultActionGroup defaultActionGroup = existingActionGroup != null ? existingActionGroup : new DefaultActionGroup();
        if (popupAction instanceof DefaultActionGroup originalGroup) {
            Set<AnAction> alreadyAdded = new HashSet<>();
            for (AnAction child : originalGroup.getChildren(ActionManager.getInstance())) {
                String id = ActionManager.getInstance().getId(child);
                if (id == null || !allowedMenuOptions.contains(id)) continue;
                if (alreadyAdded.add(child)) {
                    defaultActionGroup.add(child);
                }
            }
        }

        return defaultActionGroup;
    }
}
