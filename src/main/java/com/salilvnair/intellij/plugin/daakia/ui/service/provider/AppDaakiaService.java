package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.ui.treeStructure.Tree;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DateUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.TreeUtils;
import org.springframework.util.MultiValueMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(AppDaakiaType.INIT_HISTORY.equals(type)) {
            initHistoryRootNode(type, dataContext, objects);
        }
        else if(AppDaakiaType.ADD_HISTORY.equals(type)) {
            addHistoryData(type, dataContext, objects);
        }
        return dataContext.daakiaContext();
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


    private void addHistoryData(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        String url = dataContext.uiContext().urlTextField().getText();
        String requestType = (String) dataContext.uiContext().requestTypes().getSelectedItem();
        String requestBody = dataContext.uiContext().requestTextArea().getText();
        MultiValueMap<String, String> requestHeaders = dataContext.daakiaContext().requestHeaders();
        DaakiaHistory daakiaHistory = new DaakiaHistory();
        daakiaHistory.setRequestType(requestType);
        daakiaHistory.setUrl(url);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String headers = objectMapper.writeValueAsString(requestHeaders);
            daakiaHistory.setHeaders(headers);
        }
        catch (JsonProcessingException ex) {

        }
        daakiaHistory.setRequestBody(requestBody);
        daakiaHistory.setCreatedDate(DateUtils.todayAsString());
        dataContext.uiContext().setDaakiaHistory(daakiaHistory);
        Map<String, List<DaakiaHistory>> historyData = dataContext.uiContext().historyData();
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
}
