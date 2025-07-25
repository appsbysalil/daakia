package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.FileUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
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
        responseTextArea.setEditable(false);
        dataContext.uiContext().setResponseTextArea(responseTextArea);
        scrollPane = new RTextScrollPane(responseTextArea);
        scrollPane.setIconRowHeaderEnabled(true); // Enable icon row header for folding icons
        scrollPane.setFoldIndicatorEnabled(true); // Enable fold indicators
        scrollPane.setViewportView(responseTextArea);
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
        listen(event ->{
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RECEIVING_RESPONSE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                ResponseEntity<?> responseEntity = daakiaEvent.responseEntity();
                if(responseEntity != null) {
                    String formattedText = uiContext().downloadResponse() ? null : responseEntity.getBody() == null ? null :JsonUtils.format((String) responseEntity.getBody());
                    responseTextArea.setText(formattedText);
                    if(uiContext().downloadResponse()) {
                        FileUtils.saveResponseAsFile(responseEntity);
                    }
                }
                else if(daakiaEvent.daakiaContext() != null && daakiaEvent.daakiaContext().errorMessage() != null) {
                    responseTextArea.setText(daakiaEvent.daakiaContext().errorMessage());
                }
            }
        });
    }
}
