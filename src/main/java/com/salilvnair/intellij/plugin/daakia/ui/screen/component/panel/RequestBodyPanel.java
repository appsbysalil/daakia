package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class RequestBodyPanel extends BaseDaakiaPanel<RequestBodyPanel> {
    private RSyntaxTextArea requestTextArea;
    private RTextScrollPane scrollPane;

    public RequestBodyPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        requestTextArea = new RSyntaxTextArea();
        requestTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        requestTextArea.setCodeFoldingEnabled(true);
        dataContext.uiContext().setRequestTextArea(requestTextArea);
        scrollPane = new RTextScrollPane(requestTextArea);
        scrollPane.setIconRowHeaderEnabled(true); // Enable icon row header for folding icons
        scrollPane.setFoldIndicatorEnabled(true); // Enable fold indicators
        scrollPane.setViewportView(requestTextArea);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        super.initListeners();
    }
}
