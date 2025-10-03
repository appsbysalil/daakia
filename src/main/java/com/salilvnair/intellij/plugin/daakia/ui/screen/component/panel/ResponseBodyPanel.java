package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.json.JsonFileType;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.FileUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import java.awt.*;

public class ResponseBodyPanel extends BaseDaakiaPanel<ResponseBodyPanel> {
    private DaakiaEditorX responseTextArea;

    public ResponseBodyPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        responseTextArea = new DaakiaEditorX(JsonFileType.INSTANCE, dataContext.project(), true);
        dataContext.uiContext().setResponseTextArea(responseTextArea);
    }

    @Override
    public void initChildrenLayout() {
        add(responseTextArea, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        listen(event ->{
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_RECEIVING_RESPONSE)) {
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                ResponseEntity<?> responseEntity = daakiaEvent.responseEntity();
                if(responseEntity != null) {
                    String formattedText = uiContext().downloadResponse() ? null : responseEntity.getBody() == null ? null :JsonUtils.format((String) responseEntity.getBody());
                    responseTextArea.setText(formattedText, DaakiaUtils.resolveFileTypeFromHeaders(daakiaContext().responseHeaders()));
                    if(uiContext().downloadResponse()) {
                        FileUtils.saveResponseAsFile(dataContext.project(), responseEntity, this);
                    }
                }
                else if(daakiaEvent.daakiaContext() != null && daakiaEvent.daakiaContext().errorMessage() != null) {
                    responseTextArea.setText(daakiaEvent.daakiaContext().errorMessage(), DaakiaUtils.resolveFileTypeFromHeaders(daakiaContext().responseHeaders()));
                }
            }
        });
    }
}
