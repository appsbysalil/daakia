package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.PasswordInputField;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIContext {
    // request response components
    private Map<String, List<TextInputField>> headerTextFields;
    private Map<String, List<TextInputField>> formDataTextFields;
    private Map<String, File> formDataFileFields;
    private RSyntaxTextArea requestTextArea;
    private RSyntaxTextArea responseTextArea;
    private String requestContentType = "1";
    private ComboBox<String> requestTypes;
    private JTextField urlTextField;
    private ComboBox<Environment> environmentCombo;
    private JTable responseHeaderTable;
    private DefaultTableModel responseHeaderTableModel;
    private JLabel statusLabel;
    private JLabel sizeLabel;
    private JLabel timeLabel;

    //headers panel related components
    private JPanel headerScrollPanel;
    private JPanel headersPanel;

    //form data panel related components
    private JPanel formDataScrollPanel;
    private JPanel formDataKeyValuesPanel;

    private ComboBox<String> authTypes;
    private TextInputField userNameTextField;
    private PasswordInputField passwordTextField;
    private PasswordInputField bearerTokenTextField;

    private JProgressBar progressBar;

    private boolean downloadResponse;




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

    public ComboBox<Environment> environmentCombo() {
        return environmentCombo;
    }

    public void setEnvironmentCombo(ComboBox<Environment> environmentCombo) {
        this.environmentCombo = environmentCombo;
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

    public PasswordInputField passwordTextField() {
        return passwordTextField;
    }

    public void setPasswordTextField(PasswordInputField passwordTextField) {
        this.passwordTextField = passwordTextField;
    }

    public PasswordInputField bearerTokenTextField() {
        return bearerTokenTextField;
    }

    public void setBearerTokenTextField(PasswordInputField bearerTokenTextField) {
        this.bearerTokenTextField = bearerTokenTextField;
    }

    public JProgressBar progressBar() {
        return progressBar;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public JPanel formDataKeyValuesPanel() {
        return formDataKeyValuesPanel;
    }

    public void setFormDataKeyValuesPanel(JPanel formDataKeyValuesPanel) {
        this.formDataKeyValuesPanel = formDataKeyValuesPanel;
    }

    public JPanel formDataScrollPanel() {
        return formDataScrollPanel;
    }

    public void setFormDataScrollPanel(JPanel formDataScrollPanel) {
        this.formDataScrollPanel = formDataScrollPanel;
    }

    public void setFormDataTextFields(Map<String, List<TextInputField>> formDataTextFields) {
        this.formDataTextFields = formDataTextFields;
    }

    public Map<String, List<TextInputField>> formDataTextFields() {
        if (formDataTextFields == null) {
            formDataTextFields =  new HashMap<>();
        }
        return formDataTextFields;
    }

    public Map<String, File> formDataFileFields() {
        if (formDataFileFields == null) {
            formDataFileFields =  new HashMap<>();
        }
        return formDataFileFields;
    }

    public void setFormDataFileFields(Map<String, File> formDataFileFields) {
        this.formDataFileFields = formDataFileFields;
    }

    public String requestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public boolean downloadResponse() {
        return downloadResponse;
    }

    public void setDownloadResponse(boolean downloadResponse) {
        this.downloadResponse = downloadResponse;
    }
}
