package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class RequestScriptPanel extends BaseDaakiaPanel<RequestScriptPanel> {
    private RSyntaxTextArea preRequestArea;
    private RSyntaxTextArea postRequestArea;

    public RequestScriptPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
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

    private RSyntaxTextArea createScriptArea() {
        RSyntaxTextArea area = new RSyntaxTextArea();
        area.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        area.setCodeFoldingEnabled(true);
        return area;
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
    }

    @Override
    public void initChildrenLayout() {
        add(labeledPanel("Pre Request", preRequestArea));
        add(labeledPanel("Post Request", postRequestArea));
    }

    private JPanel labeledPanel(String label, RSyntaxTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.NORTH);
        RTextScrollPane scroll = new RTextScrollPane(area);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void initListeners() {
    }
}
