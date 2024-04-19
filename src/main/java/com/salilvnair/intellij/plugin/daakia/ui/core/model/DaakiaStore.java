package com.salilvnair.intellij.plugin.daakia.ui.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DaakiaStore {
    private String name;
    private DaakiaStoreRecord record;
    private boolean collection;
    private List<DaakiaStore> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DaakiaStoreRecord getRecord() {
        return record;
    }

    public void setRecord(DaakiaStoreRecord record) {
        this.record = record;
    }

    public List<DaakiaStore> getChildren() {
        return children;
    }

    public void setChildren(List<DaakiaStore> children) {
        this.children = children;
    }

    public boolean getCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }
}
