package com.salilvnair.intellij.plugin.daakia.ui.screen.component.custom;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.salilvnair.intellij.plugin.daakia.persistence.HistoryDao;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaBaseStoreData;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaHistory;
import com.salilvnair.intellij.plugin.daakia.ui.service.context.DataContext;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DaakiaAutoSuggestField {
    private final PlaceholderTextFieldWithAutoCompletion textField;

    public DaakiaAutoSuggestField(String placeholder, DataContext dataContext) {
        List<String> suggestions = loadHistory();
        this.textField = new PlaceholderTextFieldWithAutoCompletion(dataContext.project(), placeholder, suggestions, true, null);
    }

    public TextFieldWithAutoCompletion<String> instance() {
        return textField;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                textField.getDocument().setText(text == null ? "" : text);
            });
        });
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
}
