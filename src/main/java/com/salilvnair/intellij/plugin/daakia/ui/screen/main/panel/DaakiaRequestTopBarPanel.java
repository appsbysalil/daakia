package com.salilvnair.intellij.plugin.daakia.ui.screen.main.panel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEvent;
import com.salilvnair.intellij.plugin.daakia.ui.core.event.type.DaakiaEventType;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.dialog.EnvironmentManagerDialog;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.RestDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.GraphQlDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TextFieldUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.UrlUtils;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;

import javax.swing.*;
import javax.swing.DefaultComboBoxModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DaakiaRequestTopBarPanel extends BaseDaakiaPanel<DaakiaRequestTopBarPanel> {

    private ComboBox<String> requestTypes;
    private JTextField urlTextField;
    private JButton saveButton;
    private JButton sendButton;

    public DaakiaRequestTopBarPanel(JRootPane rootPane, DataContext dataContext) {
        super(rootPane, dataContext);
        init();
    }

    @Override
    public void initLayout() {
        setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 0));
    }

    @Override
    public void initComponents() {
        requestTypes = new ComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE", "GRAPHQL"});
        urlTextField = new JTextField();
        sendButton = new JButton("Send");
        JPopupMenu dropdownMenu = new JPopupMenu();

        JMenuItem sendMenuItem = new JMenuItem("Send");
        sendMenuItem.addActionListener(e -> sendAction());

        JMenuItem sendAndDownloadMenuItem = new JMenuItem("Send and Download");
        sendAndDownloadMenuItem.addActionListener(e -> sendAndDownloadAction());

        dropdownMenu.add(sendMenuItem);
        dropdownMenu.add(sendAndDownloadMenuItem);

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && sendButton.isEnabled()) {
                    dropdownMenu.show(sendButton, e.getX(), e.getY());
                }
            }
        });
        saveButton = new JButton("Save");
        dataContext.uiContext().setRequestTypes(requestTypes);
        dataContext.uiContext().setUrlTextField(urlTextField);
    }

    private void sendAction() {
        sendButton.setEnabled(false);
        eventPublisher().onClickSend();
        dataContext.uiContext().setDownloadResponse(false);
        if("GRAPHQL".equals(requestTypes.getSelectedItem())) {
            daakiaService(DaakiaType.GRAPHQL).execute(GraphQlDaakiaType.EXECUTE, dataContext);
        }
        else {
            daakiaService(DaakiaType.REST).execute(RestDaakiaType.EXCHANGE, dataContext);
        }
    }

    private void sendAndDownloadAction() {
        //JOptionPane.showMessageDialog(null, "Feature coming soon..!", "Coming soon...", JOptionPane.INFORMATION_MESSAGE,DaakiaIcons.DaakiaIcon48);
        sendButton.setEnabled(false);
        eventPublisher().onClickSend();
        dataContext.uiContext().setDownloadResponse(true);
        if("GRAPHQL".equals(requestTypes.getSelectedItem())) {
            daakiaService(DaakiaType.GRAPHQL).execute(GraphQlDaakiaType.EXECUTE, dataContext);
        }
        else {
            daakiaService(DaakiaType.REST).execute(RestDaakiaType.EXCHANGE, dataContext);
        }
    }

    @Override
    public void initStyle() {
        debugIfApplicable(this);
        sendButton.setIcon(AllIcons.Actions.Execute);
        saveButton.setIcon(DaakiaIcons.SaveIcon);
        sendButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    @Override
    public void initChildrenLayout() {
        add(requestTypes);
        add(urlTextField);
        add(sendButton);
        add(saveButton);
        urlTextField.setPreferredSize(new Dimension(400, 25));
    }

    @Override
    public void initListeners() {

        TextFieldUtils.addChangeListener(urlTextField, e -> {
            sendButton.setEnabled(!urlTextField.getText().isEmpty() && UrlUtils.validateURL(urlTextField.getText()));
            saveButton.setEnabled(!urlTextField.getText().isEmpty() && UrlUtils.validateURL(urlTextField.getText()));
        });

        sendButton.addActionListener(e -> {
            sendAction();
        });
        saveButton.addActionListener(e -> {
            eventPublisher().onClickSave();
            daakiaService(DaakiaType.APP).execute(AppDaakiaType.UPDATE_STORE_COLLECTION_NODE, dataContext);
            daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.SAVE_REQUEST_IN_STORE_COLLECTION, dataContext);
        });
        subscriber().subscribe(event -> {
            if(DaakiaEvent.ofType(event, DaakiaEventType.AFTER_REST_EXCHANGE)) {
                sendButton.setEnabled(true);
                DaakiaEvent daakiaEvent = DaakiaEvent.extract(event);
                eventPublisher().onReceivingResponse(daakiaEvent.daakiaContext(), daakiaEvent.daakiaContext().responseEntity());
                daakiaService(DaakiaType.APP).execute(AppDaakiaType.ADD_HISTORY, dataContext);
                globalEventPublisher().onAfterHistoryAdded();
                daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.SAVE_HISTORY, dataContext);
            }
        });
    }
}
