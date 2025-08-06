package com.salilvnair.intellij.plugin.daakia.ui.core.compatibility.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.salilvnair.intellij.plugin.daakia.ui.core.model.DaakiaStoreCollection;

import java.io.IOException;

public class DaakiaStoreCollectionDeserializer extends JsonDeserializer<DaakiaStoreCollection> {

    @Override
    public DaakiaStoreCollection deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.readValueAsTree();

        if (node.isBoolean()) {
            if (node.booleanValue()) {
                // Legacy: "collection": true
                DaakiaStoreCollection collection = new DaakiaStoreCollection();
                collection.setCollection(true);
                return collection;
            }
            else {
                // Legacy: "collection": false → Treat as no collection
                return null;
            }
        }
        else if (node.isObject()) {
            // New JSON format
            return p.getCodec().treeToValue(node, DaakiaStoreCollection.class);
        }

        return null;
    }
}