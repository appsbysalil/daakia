package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
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
        try {
            String jsonString = JsonUtils.readJsonFromFile(DaakiaUtils.historyFile());
            Map<String, List<DaakiaHistory>> historyData = JsonUtils.jsonToPojo(jsonString, new TypeReference<>() {});
            dataContext.sideNavContext().setHistoryData(historyData);
        }
        catch (IOException e) {
            dataContext.sideNavContext().setHistoryData(new LinkedHashMap<>());
        }
    }

    private static void saveHistory(DataContext dataContext) {

        try {
            String jsonString = JsonUtils.pojoToJson(dataContext.sideNavContext().historyData());
            JsonUtils.writeJsonToFile(jsonString, DaakiaUtils.historyFile());
        }
        catch (IOException e) {}
    }

    private void loadRequest(DataContext dataContext) {
        try {
            String jsonString = JsonUtils.readJsonFromFile(DaakiaUtils.storeFile());
            DaakiaStore daakiaStore = JsonUtils.jsonToPojo(jsonString, DaakiaStore.class);
            if(daakiaStore != null) {
                dataContext.sideNavContext().setDaakiaStore(daakiaStore);
                DefaultMutableTreeNode newRootNode = DaakiaUtils.convertCollectionStoreToTreeNode(daakiaStore, dataContext.sideNavContext().collectionStoreRootNode());
                dataContext.sideNavContext().setCollectionStoreRootNode(newRootNode);
            }
        }
        catch (IOException e) {

        }

    }

    private void saveRequest(DataContext dataContext) {
        try {
            DefaultMutableTreeNode rootNode = dataContext.sideNavContext().collectionStoreRootNode();
            String jsonString = JsonUtils.pojoToJson(DaakiaUtils.convertTreeToCollectionStore(rootNode));
            JsonUtils.writeJsonToFile(jsonString, DaakiaUtils.storeFile());
        }
        catch (IOException e) {}
    }

}
