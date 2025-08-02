package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.persistence.CollectionDao;
import java.util.LinkedHashMap;

public class StoreDaakiaService extends BaseDaakiaService {
    @Override
    public DaakiaContext execute(DaakiaTypeBase type, DataContext dataContext, Object... objects) {
        if(StoreDaakiaType.SAVE_HISTORY.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                saveHistory(dataContext);
            });

        }
        else if(StoreDaakiaType.LOAD_HISTORY.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                loadHistory(dataContext);
            });
        }
        else if(StoreDaakiaType.SAVE_REQUEST_IN_STORE_COLLECTION.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                saveRequest(dataContext);
            });
        }
        else if(StoreDaakiaType.MARK_REQUEST_IN_STORE_COLLECTION_FOR_DELETION.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                markRequestForDeletion(dataContext);
            });
        }
        else if(StoreDaakiaType.LOAD_STORE_COLLECTIONS.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                loadRequest(dataContext);
            });
        }


        return dataContext.daakiaContext();
    }

    private void markRequestForDeletion(DataContext dataContext) {
        saveRequest(dataContext);
        new CollectionDao().markActiveAsync(false);
    }


    private void loadHistory(DataContext dataContext) {
        new HistoryDao().loadHistoryAsync(historyData -> {
            if(historyData == null) {
                historyData = new LinkedHashMap<>();
            }
            dataContext.sideNavContext().setHistoryData(historyData);
        });
    }

    private static void saveHistory(DataContext dataContext) {
        new HistoryDao().saveHistoryAsync(dataContext.sideNavContext().historyData());
    }

    private void loadRequest(DataContext dataContext) {
        new CollectionDao().loadStoreAsync( dataContext, defaultMutableTreeNode -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                dataContext.sideNavContext().setCollectionStoreRootNode(defaultMutableTreeNode);
            });
        });
    }

    private void saveRequest(DataContext dataContext) {
        new CollectionDao().saveStoreAsync(dataContext);
    }

}
