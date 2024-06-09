package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIContext {
    // request response components
    private Map<String, List<TextInputField>> headerTextFields;
    private RSyntaxTextArea requestTextArea;
    private RSyntaxTextArea responseTextArea;
    private ComboBox<String> requestTypes;
    private JTextField urlTextField;
    private JTable responseHeaderTable;
    private DefaultTableModel responseHeaderTableModel;
    private JLabel statusLabel;
    private JLabel sizeLabel;
    private JLabel timeLabel;

    //headers panel related components
    private JPanel headerScrollPanel;
    private JPanel headersPanel;

    private ComboBox<String> authTypes;
    private TextInputField userNameTextField;
    private TextInputField passwordTextField;
    private TextInputField bearerTokenTextField;




    public Map<String, List<TextInputField>> headerTextFields() {
        if (headerTextFields == null) {
            headerTextFields =  new HashMap<>();
        }
        return headerTextFields;
    }

    public RSyntaxTextArea requestTextArea() {
        return requestTextArea;
    }

    public void setRequestTextArea(RSyntaxTextArea requestTextArea) {
        this.requestTextArea = requestTextArea;
    }

    public RSyntaxTextArea responseTextArea() {
        return responseTextArea;
    }

    public void setResponseTextArea(RSyntaxTextArea responseTextArea) {
        this.responseTextArea = responseTextArea;
    }

    public ComboBox<String> requestTypes() {
        return requestTypes;
    }

    public void setRequestTypes(ComboBox<String> requestTypes) {
        this.requestTypes = requestTypes;
    }

    public JTextField urlTextField() {
        return urlTextField;
    }

    public void setUrlTextField(JTextField urlTextField) {
        this.urlTextField = urlTextField;
    }

    public JPanel headerScrollPanel() {
        return headerScrollPanel;
    }

    public void setHeaderScrollPanel(JPanel headerScrollPanel) {
        this.headerScrollPanel = headerScrollPanel;
    }

    public JPanel headersPanel() {
        return headersPanel;
    }

    public void setHeadersPanel(JPanel headersPanel) {
        this.headersPanel = headersPanel;
    }

    public JLabel statusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    public JLabel sizeLabel() {
        return sizeLabel;
    }

    public void setSizeLabel(JLabel sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    public JLabel timeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(JLabel timeLabel) {
        this.timeLabel = timeLabel;
    }

    public JTable responseHeaderTable() {
        return responseHeaderTable;
    }

    public void setResponseHeaderTable(JTable responseHeaderTable) {
        this.responseHeaderTable = responseHeaderTable;
    }

    public DefaultTableModel responseHeaderTableModel() {
        return responseHeaderTableModel;
    }

    public void setResponseHeaderTableModel(DefaultTableModel responseHeaderTableModel) {
        this.responseHeaderTableModel = responseHeaderTableModel;
    }

    public ComboBox<String> authTypes() {
        return authTypes;
    }

    public void setAuthTypes(ComboBox<String> authTypes) {
        this.authTypes = authTypes;
    }

    public TextInputField userNameTextField() {
        return userNameTextField;
    }

    public void setUserNameTextField(TextInputField userNameTextField) {
        this.userNameTextField = userNameTextField;
    }

    public TextInputField passwordTextField() {
        return passwordTextField;
    }

    public void setPasswordTextField(TextInputField passwordTextField) {
        this.passwordTextField = passwordTextField;
    }

    public TextInputField bearerTokenTextField() {
        return bearerTokenTextField;
    }

    public void setBearerTokenTextField(TextInputField bearerTokenTextField) {
        this.bearerTokenTextField = bearerTokenTextField;
    }
}
