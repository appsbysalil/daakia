package com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel;

import com.intellij.ui.components.JBScrollPane;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel.BaseDaakiaPanel;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class RequestHeaderPanel extends BaseDaakiaPanel<RequestHeaderPanel> {
    private JPanel headerScrollPanel;
    private JPanel headersPanel;
    JBScrollPane headerScrollPane;

    public RequestHeaderPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    public void initComponents() {
        headersPanel = new JPanel();
        headerScrollPanel = new JPanel();
        headerScrollPanel.add(headersPanel);
        headerScrollPane = new JBScrollPane(headerScrollPanel);
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
        headersPanel.setLayout(new BoxLayout(headersPanel, BoxLayout.Y_AXIS));
        headerScrollPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(headerScrollPanel);
    }

    @Override
    public void initListeners() {
        subscriber().subscribe( event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.ON_CLICK_ADD_HEADER)) {
                createHeader(null, null);
            }
        });
    }



    private void createHeader(String headerKey, String headerValue) {
        String rowId = UUID.randomUUID().toString();

        TextInputField headerKeyField = new TextInputField("Header "+(uiContext().headerTextFields().size() + 1));
        headerKeyField.setPreferredSize(new Dimension(350, 25));
        headerKeyField.setText(headerKey);

        TextInputField headerValueField = new TextInputField("Value "+(uiContext().headerTextFields().size() + 1));
        headerValueField.setPreferredSize(new Dimension(600, 25));
        headerValueField.setText(headerValue);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(headerKeyField);
        headerPanel.add(headerValueField);

        JButton deleteHeaderButton = new JButton("Delete");
        headerPanel.add(deleteHeaderButton);

        headersPanel.add(headerPanel);
        headersPanel.revalidate();
        headersPanel.repaint();
        List<TextInputField> headerKeyValFields = List.of(headerKeyField, headerValueField);
        uiContext().headerTextFields().put(rowId, headerKeyValFields);
        headerScrollPanel.setVisible(true);

        // ActionListener for deleting header
        deleteHeaderButton.addActionListener(e1 -> {
            headersPanel.remove(headerPanel);
            eventPublisher().onClickDeleteHeaderRow(headerKeyField.getText());
            uiContext().headerTextFields().remove(rowId);
            headersPanel.revalidate();
            headersPanel.repaint();
            headerScrollPanel.revalidate();
            headerScrollPanel.repaint();
            if(uiContext().headerTextFields().isEmpty()) {
                headerScrollPanel.setVisible(false);
            }
        });
    }
}
