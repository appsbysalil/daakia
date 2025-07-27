package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.RawBodyTypeDropdown;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;

import javax.swing.*;
import java.awt.*;

public class RequestBodyPanel extends BaseDaakiaPanel<RequestBodyPanel> {
    private DaakiaEditorX requestTextArea;

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
        requestTextArea = new DaakiaEditorX(JsonFileType.INSTANCE, dataContext.project());
        dataContext.uiContext().setRequestTextArea(requestTextArea);
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(requestTextArea, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        listen(e -> {
            if(DaakiaEvent.ofType(e, DaakiaEventType.ON_CLICK_REQUEST_BODY_FORMATTER_BTN)) {
                if(requestTextArea.text() != null && !requestTextArea.text().isEmpty()) {
                    String formattedText = JsonUtils.format(requestTextArea.text());
                    requestTextArea.setText(formattedText);
                }
            }
            else if(DaakiaEvent.ofType(e, DaakiaEventType.ON_SELECT_RAW_BODY_TYPE)) {
                // change DaakiaEditorX fileType based on selection
                RawBodyTypeDropdown.RawType type = dataContext.uiContext().rawBodyType();
                if (type == RawBodyTypeDropdown.RawType.JSON) {
                    requestTextArea.updateFileType(JsonFileType.INSTANCE);
                }
                else if (type == RawBodyTypeDropdown.RawType.XML) {
                    requestTextArea.updateFileType(XmlFileType.INSTANCE);
                }
                else if (type == RawBodyTypeDropdown.RawType.HTML) {
                    requestTextArea.updateFileType(HtmlFileType.INSTANCE);
                }
                else if (type == RawBodyTypeDropdown.RawType.TEXT) {
                    requestTextArea.updateFileType(PlainTextFileType.INSTANCE);
                }
            }
        });
    }
}
