package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.utils.DaakiaUtils;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

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
        Map<String, List<DaakiaHistory>> historyData = new HistoryDao().loadHistory();
        if(historyData == null) {
            historyData = new LinkedHashMap<>();
        }
        dataContext.sideNavContext().setHistoryData(historyData);
    }

    private static void saveHistory(DataContext dataContext) {
        new HistoryDao().saveHistory(dataContext.sideNavContext().historyData());
    }

    private void loadRequest(DataContext dataContext) {
        DaakiaStore daakiaStore = new CollectionDao().loadStore();
        if(daakiaStore != null) {
            dataContext.sideNavContext().setDaakiaStore(daakiaStore);
            DefaultMutableTreeNode newRootNode = DaakiaUtils.convertCollectionStoreToTreeNode(daakiaStore, dataContext.sideNavContext().collectionStoreRootNode());
            dataContext.sideNavContext().setCollectionStoreRootNode(newRootNode);
        }

    }

    private void saveRequest(DataContext dataContext) {
        DefaultMutableTreeNode rootNode = dataContext.sideNavContext().collectionStoreRootNode();
        DaakiaStore store = DaakiaUtils.convertTreeToCollectionStore(rootNode);
        new CollectionDao().saveStore(store);
    }

}
