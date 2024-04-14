package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import java.awt.*;

public class ResponseBodyPanel extends BaseDaakiaPanel<ResponseBodyPanel> {
    private RSyntaxTextArea responseTextArea;
    private RTextScrollPane scrollPane;

    public ResponseBodyPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        responseTextArea = new RSyntaxTextArea();
        responseTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        responseTextArea.setCodeFoldingEnabled(true);
        dataContext.uiContext().setResponseTextArea(responseTextArea);
        scrollPane = new RTextScrollPane(responseTextArea);
        scrollPane.setIconRowHeaderEnabled(true); // Enable icon row header for folding icons
        scrollPane.setFoldIndicatorEnabled(true); // Enable fold indicators
        scrollPane.setViewportView(responseTextArea);
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
        subscriber().subscribe(event ->{
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RECEIVING_RESPONSE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                ResponseEntity<String> responseEntity = daakiaEvent.responseEntity();
                if(responseEntity != null) {
                    responseTextArea.setText(responseEntity.getBody());
                }
            }
        });
    }
}
