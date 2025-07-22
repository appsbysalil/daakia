package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.linter.JsonLintParser;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
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
        JsonLintParser jsonLintParser = new JsonLintParser();
        requestTextArea.addParser(jsonLintParser);
        dataContext.uiContext().setRequestTextArea(requestTextArea);
        scrollPane = new RTextScrollPane(requestTextArea);
        scrollPane.setIconRowHeaderEnabled(true); // Enable icon row header for folding icons
        scrollPane.setFoldIndicatorEnabled(true); // Enable fold indicators
        scrollPane.setViewportView(requestTextArea);
        scrollPane.setBorder(null);
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
        listen(e -> {
            if(DaakiaEvent.ofType(e, DaakiaEventType.ON_CLICK_REQUEST_BODY_FORMATTER_BTN)) {
                if(requestTextArea.getText() != null && !requestTextArea.getText().isEmpty()) {
                    String formattedText = JsonUtils.format(requestTextArea.getText());
                    requestTextArea.setText(formattedText);
                }
            }
        });
    }
}
