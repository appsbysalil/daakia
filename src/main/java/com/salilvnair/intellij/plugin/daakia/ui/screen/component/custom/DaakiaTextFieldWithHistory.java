package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.ui.TextFieldWithHistory;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DaakiaTextFieldWithHistory {
    private final TextFieldWithHistory field;
    private final DataContext dataContext;

    public DaakiaTextFieldWithHistory(DataContext dataContext) {
        this.dataContext = dataContext;
        this.field = new TextFieldWithHistory();
        field.setHistorySize(20); // Optional limit
        initHistory();
    }

    private void initHistory() {
        List<String> history = loadHistory();
        if (history != null) {
            field.setHistory(history);
            if (!history.isEmpty()) {
                field.setText(history.getFirst());
            }
        }
    }

    private List<String> loadHistory() {
        Map<String, List<DaakiaHistory>> historyData = new HistoryDao().loadHistory();
        return historyData
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(DaakiaBaseStoreData::getUrl)
                .toList();
    }

    public TextFieldWithHistory instance() {
        return field;
    }
}
