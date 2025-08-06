package com.salilvnair.intellij.plugin.daakia.ui.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.salilvnair.intellij.plugin.daakia.ui.core.compatibility.deserializer.DaakiaStoreCollectionDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class DaakiaStore {
    private String name;
    @JsonDeserialize(using = DaakiaStoreCollectionDeserializer.class)
    private DaakiaStoreCollection collection;
    private DaakiaStoreRecord record;
    private boolean emptyCollection;
    private List<DaakiaStore> children;

    @JsonSetter("collection")
    public void setCollection(DaakiaStoreCollection collection) {
        if (collection != null && collection.getCollectionName() == null && this.name != null) {
            collection.setCollectionName(this.name); // inject parent name
        }
        this.collection = collection;
    }

    public boolean ofTypeCollection() {
        return collection != null && collection.isCollection();
    }
}
