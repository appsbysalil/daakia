package com.salilvnair.intellij.plugin.daakia.ui.service.provider;

import com.intellij.openapi.application.ApplicationManager;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStore;
import com.salilvnair.intellij.plugin.daakia.ui.service.base.BaseDaakiaService;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DaakiaContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaTypeBase;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.StoreDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.AppDaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.type.DaakiaType;
import com.salilvnair.intellij.plugin.daakia.ui.service.factory.DaakiaFactory;
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
        else if(StoreDaakiaType.MARK_HISTORY_ENTRY_FOR_DELETION.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                markHistoryForDeletion(dataContext);
            });
        }
        else if(StoreDaakiaType.RESTORE_HISTORY_ENTRY.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                restoreHistoryEntry(dataContext);
            });
        }
        else if(StoreDaakiaType.RESTORE_STORE_COLLECTIONS.equals(type)) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                saveRequest(dataContext);
            });
        }


        return dataContext.daakiaContext();
    }

    private void markRequestForDeletion(DataContext dataContext) {
        saveRequest(dataContext);
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
        new CollectionDao().loadStoreAsync( daakiaStore -> {
            if(daakiaStore != null) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    dataContext.sideNavContext().setDaakiaStore(daakiaStore);
                    DefaultMutableTreeNode newRootNode = DaakiaUtils.convertCollectionStoreToTreeNode(daakiaStore, dataContext.sideNavContext().collectionStoreRootNode());
                    dataContext.sideNavContext().setCollectionStoreRootNode(newRootNode);
                });
            }
        });


    }

    private void saveRequest(DataContext dataContext) {
        DefaultMutableTreeNode rootNode = dataContext.sideNavContext().collectionStoreRootNode();
        DaakiaStore newStore = DaakiaUtils.convertTreeToCollectionStore(rootNode);
        DaakiaStore existing = dataContext.sideNavContext().daakiaStore();
        if(existing != null) {
            DaakiaUtils.mergeInactiveNodes(newStore, existing);
        }
        dataContext.sideNavContext().setDaakiaStore(newStore);
        new CollectionDao().saveStoreAsync(newStore);
    }

    private void markHistoryForDeletion(DataContext dataContext) {
        DaakiaHistory h = dataContext.sideNavContext().selectedDaakiaHistory();
        if(h != null) {
            new HistoryDao().markActiveAsync(h.getId(), false);
        }
    }

    private void restoreHistoryEntry(DataContext dataContext) {
        DaakiaHistory h = dataContext.sideNavContext().selectedDaakiaHistory();
        if(h != null) {
            new HistoryDao().markActiveAsync(h.getId(), true);
            loadHistory(dataContext);
            ApplicationManager.getApplication().invokeLater(() ->
                DaakiaFactory.generate(DaakiaType.APP).execute(AppDaakiaType.INIT_HISTORY, dataContext)
            );
        }
    }

}
