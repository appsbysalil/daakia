package com.salilvnair.intellij.plugin.daakia.ui.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class DaakiaStore {
    private String name;
    private DaakiaStoreCollection collection;
    private DaakiaStoreRecord record;
    private boolean emptyCollection;
    private List<DaakiaStore> children;

    public boolean ofTypeCollection() {
        return collection.isCollection();
    }
}
