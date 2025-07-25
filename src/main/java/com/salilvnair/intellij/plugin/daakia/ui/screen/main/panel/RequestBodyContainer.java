package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.RawBodyTypeDropdown;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;

import javax.swing.*;
import java.awt.*;

public class RequestBodyContainer extends BaseDaakiaPanel<RequestBodyContainer> {
    private JPanel requestBodyTypeContainer;
    private JRadioButton rawRequestBody;
    private JRadioButton formDataRequestBody;
    private String selectedOption;
    private JPanel requestBodyContainer;
    private RequestRawBodyContainer requestRawBodyContainer;
    private RequestFormDataBodyContainer requestFormDataBodyContainer;
    private RawBodyTypeDropdown rawTypeDropdown;

    public RequestBodyContainer(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new BorderLayout());
    }

    @Override
    public void initComponents() {
        rawTypeDropdown = new RawBodyTypeDropdown();
        requestRawBodyContainer = new RequestRawBodyContainer(rootPane, dataContext);
        requestFormDataBodyContainer = new RequestFormDataBodyContainer(rootPane, dataContext);
        requestBodyContainer = new JPanel();
        requestBodyContainer.setLayout(new BoxLayout(requestBodyContainer, BoxLayout.X_AXIS));
        requestBodyTypeContainer = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 0));
        rawRequestBody = new JRadioButton("raw");
        formDataRequestBody = new JRadioButton("form-data");
        rawRequestBody.setSelected(true);
        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(rawRequestBody);
        group.add(formDataRequestBody);
        requestBodyTypeContainer.add(rawRequestBody);
        requestBodyTypeContainer.add(formDataRequestBody);
        requestBodyTypeContainer.add(rawTypeDropdown);
    }

    @Override
    public void initStyle() {
    }

    @Override
    public void initChildrenLayout() {
        requestFormDataBodyContainer.setVisible(false);
        requestBodyContainer.add(requestRawBodyContainer);
        requestBodyContainer.add(requestFormDataBodyContainer);
        add(requestBodyTypeContainer, BorderLayout.NORTH);
        add(requestBodyContainer, BorderLayout.CENTER);
    }

    @Override
    public void initListeners() {
        rawTypeDropdown.addSelectionListener(() -> {
            RawBodyTypeDropdown.RawType type = rawTypeDropdown.getSelectedType();
            dataContext.uiContext().setRawBodyType(type);
            eventPublisher().onSelectRawBodyType();
        });
        // Add ActionListeners to radio buttons
        rawRequestBody.addActionListener(e -> {
            dataContext.uiContext().setRequestContentType("1");
            requestRawBodyContainer.setVisible(true);
            requestFormDataBodyContainer.setVisible(false);
        });
        formDataRequestBody.addActionListener(e -> {
            dataContext.uiContext().setRequestContentType("2");
            requestRawBodyContainer.setVisible(false);
            requestFormDataBodyContainer.setVisible(true);
        });
    }
}
