package com.salilvnair.intellij.plugin.daakia.ui.service.context;

import com.intellij.openapi.ui.ComboBox;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.Environment;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.DaakiaAutoSuggestField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.RawBodyTypeDropdown;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.editor.DaakiaEditorX;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.PasswordInputField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.settings.DaakiaSettings;
import lombok.Setter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class UIContext {
    // request response components
    private Map<String, List<TextInputField>> headerTextFields;
    private Map<String, List<TextInputField>> formDataTextFields;
    private Map<String, File> formDataFileFields;
    private DaakiaEditorX requestTextArea;
    private DaakiaEditorX responseTextArea;
    private String requestContentType = "1";
    private ComboBox<String> requestTypes;
    private DaakiaAutoSuggestField urlTextField;
    private ComboBox<Environment> environmentCombo;
    private JTable responseHeaderTable;
    private DefaultTableModel responseHeaderTableModel;
    private JLabel statusLabel;
    private JLabel sizeLabel;
    private JLabel timeLabel;
    private JLabel labelTitle;
    private String tabTitle;
    private JPanel selectedPnlTab;
    private RawBodyTypeDropdown.RawType rawBodyType;
    private JTabbedPane dynamicDaakiaTabbedPane;

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

    private DaakiaEditorX preRequestScriptArea;
    private DaakiaEditorX postRequestScriptArea;

    /** Editor used to display debug logs */
    private DaakiaEditorX debugLogEditor;
  
    private boolean downloadResponse;

    private boolean debugMode;

    private boolean scriptLogEnabled;




    public Map<String, List<TextInputField>> headerTextFields() {
        if (headerTextFields == null) {
            headerTextFields =  new HashMap<>();
        }
        return headerTextFields;
    }

    public DaakiaEditorX requestTextArea() {
        return requestTextArea;
    }

    public DaakiaEditorX responseTextArea() {
        return responseTextArea;
    }

    public ComboBox<String> requestTypes() {
        return requestTypes;
    }

    public DaakiaAutoSuggestField urlTextField() {
        return urlTextField;
    }

    public ComboBox<Environment> environmentCombo() {
        return environmentCombo;
    }

    public JPanel headerScrollPanel() {
        return headerScrollPanel;
    }

    public JPanel headersPanel() {
        return headersPanel;
    }

    public JLabel statusLabel() {
        return statusLabel;
    }

    public JLabel sizeLabel() {
        return sizeLabel;
    }

    public JLabel timeLabel() {
        return timeLabel;
    }

    public JTable responseHeaderTable() {
        return responseHeaderTable;
    }

    public DefaultTableModel responseHeaderTableModel() {
        return responseHeaderTableModel;
    }

    public ComboBox<String> authTypes() {
        return authTypes;
    }

    public TextInputField userNameTextField() {
        return userNameTextField;
    }

    public PasswordInputField passwordTextField() {
        return passwordTextField;
    }

    public PasswordInputField bearerTokenTextField() {
        return bearerTokenTextField;
    }

    public JProgressBar progressBar() {
        return progressBar;
    }

    public DaakiaEditorX preRequestScriptArea() {
        return preRequestScriptArea;
    }

    public DaakiaEditorX postRequestScriptArea() {
        return postRequestScriptArea;
    }

    public JPanel formDataKeyValuesPanel() {
        return formDataKeyValuesPanel;
    }

    public JPanel formDataScrollPanel() {
        return formDataScrollPanel;
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

    public String requestContentType() {
        return requestContentType;
    }

    public boolean downloadResponse() {
        return downloadResponse;
    }

    public DaakiaEditorX debugLogEditor() {
        return debugLogEditor;
    }

    public boolean debugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        DaakiaSettings.getInstance().getState().debugMode = debugMode;
    }

    public boolean scriptLogEnabled() {
        return scriptLogEnabled;
    }

    public void setScriptLogEnabled(boolean scriptLogEnabled) {
        this.scriptLogEnabled = scriptLogEnabled;
        DaakiaSettings.getInstance().getState().scriptLogEnabled = scriptLogEnabled;
    }

    public RawBodyTypeDropdown.RawType rawBodyType() {
        return rawBodyType;
    }

    public JPanel selectedPnlTab() {
        return selectedPnlTab;
    }

    public String tabTitle() {
        return tabTitle;
    }

    public JLabel labelTitle() {
        return labelTitle;
    }

    public JTabbedPane dynamicDaakiaTabbedPane() {
        return dynamicDaakiaTabbedPane;
    }

}
