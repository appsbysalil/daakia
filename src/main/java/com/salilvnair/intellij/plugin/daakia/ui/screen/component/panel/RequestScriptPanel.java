package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.type.DaakiaJavaScriptFileType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import javax.swing.*;
import java.awt.*;

public class RequestScriptPanel extends BaseDaakiaPanel<RequestScriptPanel> {
    private DaakiaEditorX preRequestArea;
    private DaakiaEditorX postRequestArea;

    public RequestScriptPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init(this);
    }

    @Override
    public void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public void initComponents() {
        preRequestArea = createScriptArea();
        postRequestArea = createScriptArea();
        dataContext.uiContext().setPreRequestScriptArea(preRequestArea);
        dataContext.uiContext().setPostRequestScriptArea(postRequestArea);
    }

    private DaakiaEditorX createScriptArea() {
        return new DaakiaEditorX(DaakiaJavaScriptFileType.INSTANCE, dataContext.project());
    }

    @Override
    public void initChildrenLayout() {
        add(labeledPanel("Pre Request", preRequestArea));
        add(labeledPanel("Post Request", postRequestArea));
    }

    private JPanel labeledPanel(String label, DaakiaEditorX area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void initListeners() {
    }
}
