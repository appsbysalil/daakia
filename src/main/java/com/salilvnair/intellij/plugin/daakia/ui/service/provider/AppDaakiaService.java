package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.core.icon.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.*;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.DaakiaAutoSuggestField;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.*;
import com.salilvnair.intellij.plugin.daakia.ui.utils.*;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import java.util.Base64;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AppDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(AppDaakiaType.INIT_HISTORY.equals(type)) {
            initHistoryRootNode(type, dataContext, objects);
        }
        else if(AppDaakiaType.SEARCH_HISTORY.equals(type)) {
            initHistoryRootNodeFromSearchText(type, dataContext, objects);
        }
        else if(AppDaakiaType.SEARCH_COLLECTION.equals(type)) {
            initStoreCollectionsFromSearchText(type, dataContext, objects);
        }
        else if (AppDaakiaType.INIT_STORE_COLLECTIONS.equals(type)) {
            initStoreCollections(type, dataContext, objects);
        }
        else if(AppDaakiaType.ADD_HISTORY.equals(type)) {
            addHistoryData(type, dataContext, objects);
        }
        else if(AppDaakiaType.ON_CLICK_HISTORY_NODE.equals(type)) {
            loadApplicableDaakiaUiComponentsOnClickHistoryNode(type, dataContext, objects);
        }
        else if(AppDaakiaType.ON_DBL_CLICK_HISTORY_NODE.equals(type)
            || AppDaakiaType.ON_RIGHT_CLICK_RENAME_HISTORY_NODE.equals(type)) {
            renameHistoryDisplayName(type, dataContext, objects);
        }
        else if(AppDaakiaType.ON_DBL_CLICK_STORE_COLLECTION_NODE.equals(type)
            || AppDaakiaType.ON_RIGHT_CLICK_RENAME_STORE_COLLECTION_NODE.equals(type)) {
            renameCollectionDisplayName(type, dataContext, objects);
        }
        else if(AppDaakiaType.ON_CLICK_STORE_COLLECTION_NODE.equals(type)) {
            loadApplicableDaakiaUiComponentsOnClickStoreCollectionNode(type, dataContext, objects);
        }
        else if(AppDaakiaType.CREATE_REQUEST_HEADER.equals(type)) {
            ApplicationManager.getApplication().invokeLater(() -> {
                createHeader(dataContext, (String) objects[0], (String) objects[1]);
            });
        }
        else if(AppDaakiaType.CREATE_FORM_DATA.equals(type)) {
            createFormData(dataContext, (String) objects[0], (String) objects[1]);
        }
        else if (AppDaakiaType.CREATE_RESPONSE_HEADERS.equals(type)) {
            createResponseHeaders(dataContext, objects);
        }
        else if (AppDaakiaType.CREATE_RESPONSE_STATUS.equals(type)) {
            createResponseStatus(dataContext, objects);
        }
        else if(AppDaakiaType.UPDATE_STORE_COLLECTION_NODE.equals(type)) {
            onSaveRequest(dataContext, objects);
        }
        return dataContext.daakiaContext();
    }

    private void createResponseStatus(DataContext dataContext, Object... objects) {
        ResponseMetadata responseMetadata = dataContext.daakiaContext().responseMetadata();
        HttpStatus httpStatus = dataContext.daakiaContext().httpStatus();
        String statusColor = ColorUtils.HexCode.EMERALD.hex();
        if(httpStatus !=null && httpStatus.isError()) {
            statusColor = ColorUtils.HexCode.CRIMSON.hex();
        }

        if(httpStatus !=null) {
            dataContext.uiContext().statusLabel().setText(LabelUtils.coloredText("&nbsp;&nbsp;Status: ", httpStatus.value() + " " + httpStatus.getReasonPhrase(), null, statusColor));
        }
        dataContext.uiContext().sizeLabel().setText(LabelUtils.coloredText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Size: ", responseMetadata.getSizeText(), null, statusColor));
        dataContext.uiContext().timeLabel().setText(LabelUtils.coloredText("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Time: ", responseMetadata.getTimeTaken(), null, statusColor));
    }

    private void createResponseHeaders(DataContext dataContext, Object... objects) {
        DefaultTableModel responseHeaderTableModel = dataContext.uiContext().responseHeaderTableModel();
        responseHeaderTableModel.setRowCount(0);
        dataContext.daakiaContext().responseHeaders().forEach((key, value) -> {
            responseHeaderTableModel.addRow(new Object[]{key, value!=null ? String.join(";", value) : null});
        });
        responseHeaderTableModel.fireTableDataChanged();
    }

    private void renameHistoryDisplayName(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        String displayName = (String) JOptionPane
                .showInputDialog(
                        dataContext.sideNavContext().collectionStoreTreePanel(),
                        "Enter a name",
                        "",
                        JOptionPane.QUESTION_MESSAGE,
                        DaakiaIcons.HttpRequestsFiletype48,
                        null,
                        null);
        dataContext.sideNavContext().selectedDaakiaHistory().setDisplayName(displayName);
    }

    private void renameCollectionDisplayName(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        String displayName = (String) JOptionPane
                .showInputDialog(
                        dataContext.sideNavContext().collectionStoreTreePanel(),
                        "Enter a name",
                        "",
                        JOptionPane.QUESTION_MESSAGE,
                        DaakiaIcons.HttpRequestsFiletype48,
                        null,
                        null);
        dataContext.sideNavContext().selectedDaakiaStoreRecord().setDisplayName(displayName);
    }

    private void initStoreCollections(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        new CollectionDao().loadStoreAsync(dataContext, rootNode -> {
            dataContext.sideNavContext().setCollectionStoreRootNode(rootNode);
            Tree tree = dataContext.sideNavContext().collectionStoreTree();
            if(tree != null) {
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                ApplicationManager.getApplication().invokeLater(() -> {
                    DefaultMutableTreeNode latestRootNode = dataContext.sideNavContext().collectionStoreRootNode();
                    model.setRoot(latestRootNode);
                    model.reload();
                });
            }
        });
    }

    private void onSaveRequest(DataContext dataContext, Object... objects) {
        if(!TreeUtils.selectedNodeIsRootNode(dataContext.sideNavContext().collectionStoreTree())) {
            System.out.println("lets add this request to the Tree");
            String displayName = (String) JOptionPane
                    .showInputDialog(
                            dataContext.sideNavContext().collectionStoreTreePanel(),
                            "Enter a name",
                            "",
                            JOptionPane.QUESTION_MESSAGE,
                            DaakiaIcons.HttpRequestsFiletype48,
                            null,
                            null);
            if(displayName!=null && !displayName.isEmpty()) {
                DaakiaStoreRecord daakiaStoreRecord = generateStoreData(dataContext, DaakiaStoreRecord.class);
                assert daakiaStoreRecord != null;
                daakiaStoreRecord.setDisplayName(displayName);
                DefaultMutableTreeNode insertionNode = TreeUtils.parentNode(dataContext.sideNavContext().collectionStoreTree(), DaakiaStoreRecord.class);
                insertionNode.add(new DefaultMutableTreeNode(daakiaStoreRecord));
                dataContext.sideNavContext().collectionStoreTreeModel().nodesWereInserted(insertionNode, new int[]{insertionNode.getChildCount() - 1});
            }
        }
        else {
            System.out.println("Alert user saying : please select a collection to store the request.");
            JOptionPane.showMessageDialog(dataContext.sideNavContext().collectionStoreTreePanel(), "Please select a collection to store the request.", "Alert", JOptionPane.ERROR_MESSAGE, DaakiaIcons.ErrorIcon48);
        }
    }

    private void loadApplicableDaakiaUiComponentsOnClickHistoryNode(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        DaakiaHistory daakiaHistory = dataContext.sideNavContext().selectedDaakiaHistory();
        loadApplicableDaakiaUiComponents(dataContext, daakiaHistory, objects);
    }

    private void loadApplicableDaakiaUiComponentsOnClickStoreCollectionNode(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        DaakiaStoreRecord daakiaStoreRecord = dataContext.sideNavContext().selectedDaakiaStoreRecord();
        loadApplicableDaakiaUiComponents(dataContext, daakiaStoreRecord, objects);
    }

    private void loadApplicableDaakiaUiComponents(DataContext dataContext, DaakiaBaseStoreData baseStoreData, Object... objects) {
        dataContext.uiContext().requestTextArea().setText(baseStoreData.getRequestBody());
        dataContext.uiContext().responseTextArea().setText(baseStoreData.getResponseBody());
        if(dataContext.uiContext().preRequestScriptArea()!=null) {
            dataContext.uiContext().preRequestScriptArea().setText(baseStoreData.getPreRequestScript());
        }
        if(dataContext.uiContext().postRequestScriptArea()!=null) {
            dataContext.uiContext().postRequestScriptArea().setText(baseStoreData.getPostRequestScript());
        }
        dataContext.uiContext().requestTypes().setSelectedItem(baseStoreData.getRequestType());
        dataContext.uiContext().urlTextField().setText(baseStoreData.getUrl());
        String headersJsonString = CryptoUtils.decrypt(baseStoreData.getHeaders());
        String responseHeadersJsonString = baseStoreData.getResponseHeaders();
        MultiValueMap<String, String> requestHeaders = JsonUtils.jsonStringToMultivaluedMap(headersJsonString);
        MultiValueMap<String, String> responseHeaders = JsonUtils.jsonStringToMultivaluedMap(responseHeadersJsonString);
        dataContext.daakiaContext().setRequestHeaders(requestHeaders);
        dataContext.daakiaContext().setResponseHeaders(responseHeaders);
        if(baseStoreData.getStatusCode()!=0) {
            dataContext.daakiaContext().setHttpStatus(HttpStatus.valueOf(baseStoreData.getStatusCode()));
        }
        dataContext.daakiaContext().setResponseMetadata(new ResponseMetadata(baseStoreData.getStatusCode(), baseStoreData.getTimeTaken(), baseStoreData.getSizeText()));
        dataContext.uiContext().headerTextFields().clear();
        dataContext.uiContext().headersPanel().removeAll();
        createRequestHeaders(dataContext, objects);
        loadAuthorizationPanel(requestHeaders, dataContext);
        createResponseHeaders(dataContext, objects);
        createResponseStatus(dataContext, objects);
    }

    private void createRequestHeaders(DataContext dataContext, Object... objects) {
        MultiValueMap<String, String> requestHeaders = dataContext.daakiaContext().requestHeaders();
        requestHeaders.forEach((key, values) -> {
            createHeader(dataContext, key, values.getFirst());
        });
    }

    private void loadAuthorizationPanel(MultiValueMap<String, String> requestHeaders, DataContext dataContext) {
        if(requestHeaders.containsKey(AuthorizationType.Constant.AUTHORIZATION)) {
            String value = requestHeaders.getFirst(AuthorizationType.Constant.AUTHORIZATION);
            if(value != null) {
                if(value.startsWith(AuthorizationType.Constant.BEARER_SPACE)) {
                    dataContext.uiContext().authTypes().setSelectedItem(AuthorizationType.BEARER_TOKEN.type());
                    dataContext.uiContext().bearerTokenTextField().setText(value.substring(7));
                }
                else if(value.startsWith(AuthorizationType.Constant.BASIC_SPACE)) {
                    try {
                        String decoded = new String(Base64.getDecoder().decode(value.substring(6)));
                        String[] parts = decoded.split(":",2);
                        if(parts.length==2) {
                            dataContext.uiContext().authTypes().setSelectedItem(AuthorizationType.BASIC_AUTH.type());
                            dataContext.uiContext().userNameTextField().setText(parts[0]);
                            dataContext.uiContext().passwordTextField().setText(parts[1]);
                        }
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    private void initStoreCollectionsFromSearchText(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        try {
            String searchText = (String) objects[0];

            // New async load returning a tree node
            new CollectionDao().loadStoreAsync(dataContext, collectionStoreRootNode -> {
                if (collectionStoreRootNode != null) {
                    DefaultMutableTreeNode rootNode;

                    if (searchText == null || searchText.isEmpty()) {
                        // No filtering, just use the loaded tree
                        rootNode = collectionStoreRootNode;
                    } else {
                        // Filter by search text
                        rootNode = DaakiaUtils.filterTreeBySearchText(collectionStoreRootNode, searchText);
                    }

                    Tree collectionStoreTree = dataContext.sideNavContext().collectionStoreTree();
                    DefaultTreeModel treeModel = (DefaultTreeModel) collectionStoreTree.getModel();

                    ApplicationManager.getApplication().invokeLater(() -> {
                        treeModel.setRoot(rootNode);
                        treeModel.reload();
                        TreeUtils.expandAllNodes(collectionStoreTree);
                    });
                }
            });

        } catch (Exception ignore) {}
    }


    private void initHistoryRootNodeFromSearchText(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        Map<String, List<DaakiaHistory>> historyData = dataContext.sideNavContext().historyData();
        String searchText = (String) objects[0];
        Map<String, List<DaakiaHistory>> filteredHistoryData = new HashMap<>();
        historyData.forEach( (yr, hDataList) -> {
            List<DaakiaHistory> filteredList = hDataList
                                                .stream()
                                                .filter(hData -> hData.getDisplayName() !=null && hData.getDisplayName().contains(searchText)
                                                        || hData.getUrl() !=null && hData.getUrl().contains(searchText)
                                                )
                                                .toList();
            if(!filteredList.isEmpty()) {
                filteredHistoryData.put(yr, filteredList);
            }
        });
        DefaultMutableTreeNode rootNode;
        if(searchText == null || searchText.isEmpty()) {
            rootNode = generateRootNodeFromHistoryData(historyData);
        }
        else {
            rootNode = generateRootNodeFromHistoryData(filteredHistoryData);
        }
        Tree historyTree = dataContext.sideNavContext().historyTree();
        // Update the tree model with new data
        DefaultTreeModel treeModel = (DefaultTreeModel) historyTree.getModel();
        treeModel.setRoot(rootNode);
        treeModel.reload();
    }


    private void initHistoryRootNode(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        new HistoryDao().loadHistoryAsync(historyData -> {
            if(historyData == null) {
                historyData = new LinkedHashMap<>();
            }
            dataContext.sideNavContext().setHistoryData(historyData);
            DefaultMutableTreeNode rootNode = generateRootNodeFromHistoryData(historyData);
            dataContext.sideNavContext().setHistoryRootNode(rootNode);
            Tree historyTree = dataContext.sideNavContext().historyTree();
            if(historyTree != null) {
                DefaultTreeModel treeModel = (DefaultTreeModel) historyTree.getModel();
                ApplicationManager.getApplication().invokeLater(() -> {
                    treeModel.setRoot(rootNode);
                    treeModel.reload();
                });
            }
        });
    }

    private DefaultMutableTreeNode generateRootNodeFromHistoryData(Map<String, List<DaakiaHistory>> historyData) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        List<String> historyYears = initHistoryYears(historyData);
        Map<String, DefaultMutableTreeNode> yearNodes = new LinkedHashMap<>();
        for (String historyYear : historyYears) {
            DefaultMutableTreeNode yearNode = new DefaultMutableTreeNode(historyYear);
            yearNodes.put(historyYear, yearNode);
        }
        for (Map.Entry<String, List<DaakiaHistory>> entry : historyData.entrySet()) {
            String date = entry.getKey();
            DefaultMutableTreeNode yearNode = yearNodes.get(DateUtils.yearFromDateString(date));
            DefaultMutableTreeNode dateNode = new DefaultMutableTreeNode(date);
            for (DaakiaHistory rowEntry : entry.getValue()) {
                dateNode.add(new DefaultMutableTreeNode(rowEntry));
            }
            yearNode.add(dateNode);
        }

        for (String year : yearNodes.keySet()) {
            rootNode.add(yearNodes.get(year));
        }
        return rootNode;
    }

    private List<String> initHistoryYears(Map<String, List<DaakiaHistory>> historyData) {
        if(historyData.isEmpty()) {
            return new ArrayList<>();
        }
        return historyData.keySet().stream().map(DateUtils::yearFromDateString).collect(Collectors.toList());
    }

    private <T> T generateStoreData(DataContext dataContext, Class<T> clazz) {
        DaakiaBaseStoreData daakiaBaseStoreData = new DaakiaBaseStoreData();
        String url = dataContext.uiContext().urlTextField().getText();
        String requestType = (String) dataContext.uiContext().requestTypes().getSelectedItem();
        String requestBody = dataContext.uiContext().requestTextArea().getText();
        String responseBody = dataContext.uiContext().responseTextArea().getText();
        String preScript = dataContext.uiContext().preRequestScriptArea() != null ?
                dataContext.uiContext().preRequestScriptArea().getText() : null;
        String postScript = dataContext.uiContext().postRequestScriptArea() != null ?
                dataContext.uiContext().postRequestScriptArea().getText() : null;
        MultiValueMap<String, String> requestHeaders = dataContext.daakiaContext().requestHeaders();
        MultiValueMap<String, String> responseHeaders = dataContext.daakiaContext().responseHeaders();
        ResponseMetadata responseMetadata = dataContext.daakiaContext().responseMetadata();
        String headers;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            headers = objectMapper.writeValueAsString(requestHeaders);
            headers = CryptoUtils.encrypt(headers);
            daakiaBaseStoreData.setUuid(UUID.randomUUID().toString());
            daakiaBaseStoreData.setHeaders(headers);
            daakiaBaseStoreData.setResponseHeaders(objectMapper.writeValueAsString(responseHeaders));
            daakiaBaseStoreData.setRequestType(requestType);
            daakiaBaseStoreData.setUrl(url);
            daakiaBaseStoreData.setRequestBody(requestBody);
            daakiaBaseStoreData.setResponseBody(responseBody);
            daakiaBaseStoreData.setPreRequestScript(preScript);
            daakiaBaseStoreData.setPostRequestScript(postScript);
            daakiaBaseStoreData.setCreatedDate(DateUtils.todayAsString());
            daakiaBaseStoreData.setStatusCode(responseMetadata.getStatusCode());
            daakiaBaseStoreData.setTimeTaken(responseMetadata.getTimeTaken());
            daakiaBaseStoreData.setSizeText(responseMetadata.getSizeText());
            String storeString = objectMapper.writeValueAsString(daakiaBaseStoreData);
            return JsonUtils.jsonToPojo(storeString, clazz);
        }
        catch (Exception e) {

        }
        return null;
    }


    private void addHistoryData(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        DaakiaHistory daakiaHistory = generateStoreData(dataContext, DaakiaHistory.class);
        dataContext.sideNavContext().setDaakiaHistory(daakiaHistory);
        Map<String, List<DaakiaHistory>> historyData = dataContext.sideNavContext().historyData();
        assert daakiaHistory != null;
        String date = daakiaHistory.getCreatedDate();
        Tree historyTree = dataContext.sideNavContext().historyTree();
        if (historyData.containsKey(date)) {
            // If the date exists, add the new entry to the corresponding list of entries
            List<DaakiaHistory> entries = historyData.get(date);
            entries.add(daakiaHistory);
        }
        else {
            // If the date doesn't exist, create a new list and add the new entry to it
            List<DaakiaHistory> entries = new ArrayList<>();
            entries.add(daakiaHistory);
            historyData.put(date, entries);
        }

        // Update the tree model with new data
        DefaultTreeModel treeModel = (DefaultTreeModel) historyTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        DefaultMutableTreeNode yearNode = TreeUtils.findValueNode(root, DateUtils.yearFromDateString(date));
        if (yearNode != null) {
            DefaultMutableTreeNode dateNode = TreeUtils.findValueNode(yearNode, date);
            if (dateNode != null) {
                dateNode.add(new DefaultMutableTreeNode(daakiaHistory));
                treeModel.nodesWereInserted(dateNode, new int[]{dateNode.getChildCount() - 1});
            }
            else {
                dateNode = new DefaultMutableTreeNode(date);
                dateNode.add(new DefaultMutableTreeNode(daakiaHistory));
                yearNode.add(dateNode);
                treeModel.nodesWereInserted(yearNode, new int[]{yearNode.getIndex(dateNode)});
            }
        }
        else {
            yearNode = new DefaultMutableTreeNode(DateUtils.yearFromDateString(date));
            DefaultMutableTreeNode dateNode = new DefaultMutableTreeNode(date);
            dateNode.add(new DefaultMutableTreeNode(daakiaHistory));
            yearNode.add(dateNode);
            root.add(yearNode);
            treeModel.nodesWereInserted(root, new int[]{root.getIndex(yearNode)});
        }
    }

    private void createHeader(DataContext dataContext, String headerKey, String headerValue) {
        String rowId = UUID.randomUUID().toString();
        DaakiaAutoSuggestField headerKeyField = new DaakiaAutoSuggestField("Header "+(dataContext.uiContext().headerTextFields().size() + 1), HttpHeaderKey.headerKeys(), dataContext);
        headerKeyField.setPreferredSize(new Dimension(300, 35));
        headerKeyField.setText(headerKey);

        DaakiaAutoSuggestField headerValueField = new DaakiaAutoSuggestField("Value "+(dataContext.uiContext().headerTextFields().size() + 1), HttpHeaderValue.headerValues(), dataContext);
        headerValueField.setPreferredSize(new Dimension(320, 35));
        headerValueField.setText(headerValue);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(headerKeyField.instance());
        headerPanel.add(headerValueField.instance());

        IconButton deleteHeaderButton = new IconButton(DaakiaIcons.DeleteIcon, new Dimension(40, 45));
        headerPanel.add(deleteHeaderButton);
        JPanel headersPanel = dataContext.uiContext().headersPanel();
        JPanel headerScrollPanel = dataContext.uiContext().headerScrollPanel();
        headersPanel.add(headerPanel);
        headersPanel.revalidate();
        headersPanel.repaint();
        List<DaakiaAutoSuggestField> headerKeyValFields = List.of(headerKeyField, headerValueField);
        dataContext.uiContext().headerTextFields().put(rowId, headerKeyValFields);
        headerScrollPanel.setVisible(true);

        // ActionListener for deleting header
        deleteHeaderButton.addActionListener(e1 -> {
            headersPanel.remove(headerPanel);
            dataContext.eventPublisher().onClickDeleteHeaderRow(headerKeyField.getText());
            dataContext.uiContext().headerTextFields().remove(rowId);
            headersPanel.revalidate();
            headersPanel.repaint();
            headerScrollPanel.revalidate();
            headerScrollPanel.repaint();
            if(dataContext.uiContext().headerTextFields().isEmpty()) {
                headerScrollPanel.setVisible(false);
            }
        });
    }

    private void createFormData(DataContext dataContext, String headerKey, String headerValue) {
        String rowId = UUID.randomUUID().toString();

        TextInputField headerKeyField = new TextInputField("Key "+(dataContext.uiContext().formDataTextFields().size() + 1));
        headerKeyField.setPreferredSize(new Dimension(200, 35));
        headerKeyField.setText(headerKey);

        JPanel valuePanel = new JPanel();

        TextInputField headerValueField = new TextInputField("Value "+(dataContext.uiContext().formDataTextFields().size() + 1));
        headerValueField.setPreferredSize(new Dimension(315, 35));
        headerValueField.setText(headerValue);
        valuePanel.add(headerValueField);

        JPanel uploadFileBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 0));
        JButton uploadFile = new JButton("Choose File");
        JTextField filePathField = new JTextField("");
        filePathField.setPreferredSize(new Dimension(180, 35));
        uploadFileBtnPanel.add(uploadFile);
        uploadFileBtnPanel.add(filePathField);
        valuePanel.add(uploadFileBtnPanel);

        uploadFile.addActionListener(e1 -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getName());
                dataContext.uiContext().formDataFileFields().put(headerKey, selectedFile);
            }
        });

        JPanel formDataRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel formDataKeyWithTypePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        formDataKeyWithTypePanel.add(headerKeyField);
        ComboBox<String> formDataTypes = new ComboBox<>(new String[]{"File", "Text"});
        formDataTypes.setSelectedIndex(0);
        formDataKeyWithTypePanel.add(formDataTypes);
//        Border lineBorder = BorderFactory.createLineBorder(JBColor.BLACK, 1);
//        formDataKeyWithTypePanel.setBorder(lineBorder);
//        valuePanel.setBorder(lineBorder);
        formDataRowPanel.add(formDataKeyWithTypePanel);
        formDataRowPanel.add(valuePanel);

        String formDataType = (String) formDataTypes.getSelectedItem();

        showHideTextFieldFileUpload(formDataType, headerValueField, uploadFileBtnPanel);

        formDataTypes.addActionListener(e -> {
            String formDataType1 = (String) formDataTypes.getSelectedItem();
            showHideTextFieldFileUpload(formDataType1, headerValueField, uploadFileBtnPanel);
        });



        IconButton deleteFormDataKeyButton = new IconButton(DaakiaIcons.DeleteIcon, new Dimension(30, 25));
        formDataRowPanel.add(deleteFormDataKeyButton);
        JPanel formDataKeyValuesPanel = dataContext.uiContext().formDataKeyValuesPanel();
        JPanel formDataScrollPanel = dataContext.uiContext().formDataScrollPanel();
        formDataKeyValuesPanel.add(formDataRowPanel);
        formDataKeyValuesPanel.revalidate();
        formDataKeyValuesPanel.repaint();
        List<TextInputField> headerKeyValFields = List.of(headerKeyField, headerValueField);
        dataContext.uiContext().formDataTextFields().put(rowId, headerKeyValFields);
        formDataScrollPanel.setVisible(true);

        // ActionListener for deleting header
        deleteFormDataKeyButton.addActionListener(e1 -> {
            formDataKeyValuesPanel.remove(formDataRowPanel);
            dataContext.eventPublisher().onClickDeleteFormDataKeyValueRow(headerKeyField.getText());
            dataContext.uiContext().formDataTextFields().remove(rowId);
            formDataKeyValuesPanel.revalidate();
            formDataKeyValuesPanel.repaint();
            formDataScrollPanel.revalidate();
            formDataScrollPanel.repaint();
            if(dataContext.uiContext().formDataTextFields().isEmpty()) {
                formDataScrollPanel.setVisible(false);
            }
        });
    }

    private void showHideTextFieldFileUpload(String formDataType, TextInputField headerValueField, JPanel uploadFileBtnPanel) {
        if(formDataType !=null && formDataType.equals("File")) {
            headerValueField.setVisible(false);
            uploadFileBtnPanel.setVisible(true);
        }
        if(formDataType !=null && formDataType.equals("Text")) {
            headerValueField.setVisible(true);
            uploadFileBtnPanel.setVisible(false);
        }
    }
}
