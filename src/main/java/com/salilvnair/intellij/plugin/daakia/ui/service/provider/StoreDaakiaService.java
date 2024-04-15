package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.archive.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.IntellijUtils;
import com.salilvnair.intellij.plugin.daakia.ui.utils.JsonUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StoreDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(StoreDaakiaType.SAVE_HISTORY.equals(type)) {
            saveHistory(dataContext);
        }
        else if(StoreDaakiaType.LOAD_HISTORY.equals(type)) {
            loadHistory(dataContext);
        }
        else if(StoreDaakiaType.SAVE_REQUEST_IN_STORE_COLLECTION.equals(type)) {
            saveRequest(dataContext);
        }
        else if(StoreDaakiaType.LOAD_STORE_COLLECTIONS.equals(type)) {
            loadRequest(dataContext);
        }


        return dataContext.daakiaContext();
    }



    private void loadHistory(DataContext dataContext) {
        String pluginPath = IntellijUtils.pluginPath();

        try {
            String jsonString = JsonUtils.readJsonFromFile(pluginPath+"/daakia-history.json");
            Map<String, List<DaakiaHistory>> historyData = JsonUtils.jsonToPojo(jsonString, new TypeReference<>() {});
            dataContext.uiContext().setHistoryData(historyData);
        }
        catch (IOException e) {
            dataContext.uiContext().setHistoryData(new LinkedHashMap<>());
        }
    }

    private static void saveHistory(DataContext dataContext) {
        String pluginPath = IntellijUtils.pluginPath();

        try {
            String jsonString = JsonUtils.pojoToJson(dataContext.uiContext().historyData());
            JsonUtils.writeJsonToFile(jsonString, pluginPath+"/daakia-history.json");
        }
        catch (IOException e) {}
    }

    private void loadRequest(DataContext dataContext) {
        String pluginPath = IntellijUtils.pluginPath();

        try {
            String jsonString = JsonUtils.readJsonFromFile(pluginPath+"/daakia-collections.json");
            DaakiaStore daakiaStore = JsonUtils.jsonToPojo(jsonString, DaakiaStore.class);
            if(daakiaStore != null) {
                dataContext.uiContext().setDaakiaStore(daakiaStore);
                DefaultMutableTreeNode newRootNode = DaakiaUtils.convertCollectionStoreToTreeNode(daakiaStore, dataContext.uiContext().collectionStoreRootNode());
                dataContext.uiContext().setCollectionStoreRootNode(newRootNode);
            }
        }
        catch (IOException e) {

        }

    }

    private void saveRequest(DataContext dataContext) {
        String pluginPath = IntellijUtils.pluginPath();
        try {
            DefaultMutableTreeNode rootNode = dataContext.uiContext().collectionStoreRootNode();
            String jsonString = JsonUtils.pojoToJson(DaakiaUtils.convertTreeToCollectionStore(rootNode));
            JsonUtils.writeJsonToFile(jsonString, pluginPath+"/daakia-collections.json");
        }
        catch (IOException e) {}
    }

}
