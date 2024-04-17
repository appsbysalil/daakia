package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.DaakiaIcons;
import com.salilvnair.intellij.plugin.daakia.ui.archive.util.TextInputField;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreRecord;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.ResponseMetadata;
import com.salilvnair.intellij.plugin.daakia.ui.screen.component.panel.IconButton;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DateUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.LabelUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AppDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(AppDaakiaType.INIT_HISTORY.equals(type)) {
            initHistoryRootNode(type, dataContext, objects);
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
            createHeader(dataContext, (String) objects[0], (String) objects[1]);
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
        String statusColor = "#10b97e";
        if(httpStatus.isError()) {
            statusColor = "#ef4444";
        }

        dataContext.uiContext().statusLabel().setText(LabelUtils.colorText("Status: ", httpStatus.value() + " " + httpStatus.getReasonPhrase(), null, statusColor));
        dataContext.uiContext().sizeLabel().setText(LabelUtils.colorText("Size: ", responseMetadata.getSizeText(), null, statusColor));
        dataContext.uiContext().timeLabel().setText(LabelUtils.colorText("Time: ", responseMetadata.getTimeTaken(), null, statusColor));
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
                        dataContext.uiContext().collectionStoreTreePanel(),
                        "Enter a name",
                        "",
                        JOptionPane.QUESTION_MESSAGE,
                        DaakiaIcons.HttpRequestsFiletype48,
                        null,
                        null);
        dataContext.uiContext().selectedDaakiaHistory().setDisplayName(displayName);
    }

    private void renameCollectionDisplayName(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        String displayName = (String) JOptionPane
                .showInputDialog(
                        dataContext.uiContext().collectionStoreTreePanel(),
                        "Enter a name",
                        "",
                        JOptionPane.QUESTION_MESSAGE,
                        DaakiaIcons.HttpRequestsFiletype48,
                        null,
                        null);
        dataContext.uiContext().selectedDaakiaStoreRecord().setDisplayName(displayName);
    }

    private void initStoreCollections(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        dataContext.daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.LOAD_STORE_COLLECTIONS, dataContext);
    }

    private void onSaveRequest(DataContext dataContext, Object... objects) {
        if(!TreeUtils.selectedNodeIsRootNode(dataContext.uiContext().collectionStoreTree())) {
            System.out.println("lets add this request to the Tree");
            String displayName = (String) JOptionPane
                    .showInputDialog(
                            dataContext.uiContext().collectionStoreTreePanel(),
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
                DefaultMutableTreeNode insertionNode = TreeUtils.parentNode(dataContext.uiContext().collectionStoreTree(), DaakiaStoreRecord.class);
                insertionNode.add(new DefaultMutableTreeNode(daakiaStoreRecord));
                dataContext.uiContext().collectionStoreTreeModel().nodesWereInserted(insertionNode, new int[]{insertionNode.getChildCount() - 1});
            }
        }
        else {
            System.out.println("Alert user saying : please select a collection to store the request.");
            JOptionPane.showMessageDialog(dataContext.uiContext().collectionStoreTreePanel(), "Please select a collection to store the request.", "Alert", JOptionPane.ERROR_MESSAGE, DaakiaIcons.ErrorIcon48);
        }
    }

    private void loadApplicableDaakiaUiComponentsOnClickHistoryNode(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        DaakiaHistory daakiaHistory = dataContext.uiContext().selectedDaakiaHistory();
        loadApplicableDaakiaUiComponents(dataContext, daakiaHistory, objects);
    }

    private void loadApplicableDaakiaUiComponentsOnClickStoreCollectionNode(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        DaakiaStoreRecord daakiaStoreRecord = dataContext.uiContext().selectedDaakiaStoreRecord();
        loadApplicableDaakiaUiComponents(dataContext, daakiaStoreRecord, objects);
    }

    private void loadApplicableDaakiaUiComponents(DataContext dataContext, DaakiaBaseStoreData baseStoreData, Object... objects) {
        dataContext.uiContext().requestTextArea().setText(baseStoreData.getRequestBody());
        dataContext.uiContext().responseTextArea().setText(baseStoreData.getResponseBody());
        dataContext.uiContext().requestTypes().setSelectedItem(baseStoreData.getRequestType());
        dataContext.uiContext().urlTextField().setText(baseStoreData.getUrl());
        String headersJsonString = baseStoreData.getHeaders();
        String responseHeadersJsonString = baseStoreData.getResponseHeaders();
        MultiValueMap<String, String> requestHeaders = JsonUtils.jsonStringToMultivaluedMap(headersJsonString);
        MultiValueMap<String, String> responseHeaders = JsonUtils.jsonStringToMultivaluedMap(responseHeadersJsonString);
        dataContext.daakiaContext().setRequestHeaders(requestHeaders);
        dataContext.daakiaContext().setResponseHeaders(responseHeaders);
        dataContext.daakiaContext().setHttpStatus(HttpStatus.valueOf(baseStoreData.getStatusCode()));
        dataContext.daakiaContext().setResponseMetadata(new ResponseMetadata(baseStoreData.getStatusCode(), baseStoreData.getTimeTaken(), baseStoreData.getSizeText()));
        dataContext.uiContext().headerTextFields().clear();
        dataContext.uiContext().headersPanel().removeAll();
        createRequestHeaders(dataContext, objects);
        createResponseHeaders(dataContext, objects);
        createResponseStatus(dataContext, objects);
    }

    private void createRequestHeaders(DataContext dataContext, Object... objects) {
        MultiValueMap<String, String> requestHeaders = dataContext.daakiaContext().requestHeaders();
        requestHeaders.forEach((key, values) -> {
            createHeader(dataContext, key, values.get(0));
        });
    }

    private void initHistoryRootNode(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        dataContext.daakiaService(DaakiaType.STORE).execute(StoreDaakiaType.LOAD_HISTORY, dataContext);
        Map<String, List<DaakiaHistory>> historyData = dataContext.uiContext().historyData();
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
        dataContext.uiContext().setHistoryRootNode(rootNode);
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
        MultiValueMap<String, String> requestHeaders = dataContext.daakiaContext().requestHeaders();
        MultiValueMap<String, String> responseHeaders = dataContext.daakiaContext().responseHeaders();
        ResponseMetadata responseMetadata = dataContext.daakiaContext().responseMetadata();
        String headers;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            headers = objectMapper.writeValueAsString(requestHeaders);
            daakiaBaseStoreData.setUuid(UUID.randomUUID().toString());
            daakiaBaseStoreData.setHeaders(headers);
            daakiaBaseStoreData.setResponseHeaders(objectMapper.writeValueAsString(responseHeaders));
            daakiaBaseStoreData.setRequestType(requestType);
            daakiaBaseStoreData.setUrl(url);
            daakiaBaseStoreData.setRequestBody(requestBody);
            daakiaBaseStoreData.setResponseBody(responseBody);
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
        dataContext.uiContext().setDaakiaHistory(daakiaHistory);
        Map<String, List<DaakiaHistory>> historyData = dataContext.uiContext().historyData();
        assert daakiaHistory != null;
        String date = daakiaHistory.getCreatedDate();
        Tree historyTree = dataContext.uiContext().historyTree();
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

        TextInputField headerKeyField = new TextInputField("Header "+(dataContext.uiContext().headerTextFields().size() + 1));
        headerKeyField.setPreferredSize(new Dimension(300, 25));
        headerKeyField.setText(headerKey);

        TextInputField headerValueField = new TextInputField("Value "+(dataContext.uiContext().headerTextFields().size() + 1));
        headerValueField.setPreferredSize(new Dimension(320, 25));
        headerValueField.setText(headerValue);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(headerKeyField);
        headerPanel.add(headerValueField);

        IconButton deleteHeaderButton = new IconButton(DaakiaIcons.DeleteIcon, new Dimension(30, 25));
        headerPanel.add(deleteHeaderButton);
        JPanel headersPanel = dataContext.uiContext().headersPanel();
        JPanel headerScrollPanel = dataContext.uiContext().headerScrollPanel();
        headersPanel.add(headerPanel);
        headersPanel.revalidate();
        headersPanel.repaint();
        List<TextInputField> headerKeyValFields = List.of(headerKeyField, headerValueField);
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
}
