package com.adamk33n3r.nodegraph;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class NodeTypeRegistry {

    @FunctionalInterface
    public interface NodeFactory<N extends Node> {
        N create(JsonObject json, Gson gson);
    }

    @FunctionalInterface
    public interface ExtraSerializer<N extends Node> {
        void serialize(N node, JsonObject target, Gson gson);
    }

    private static class NodeEntry<N extends Node> {
        final String label;
        final NodeFactory<N> factory;
        final ExtraSerializer<N> extraSerializer;

        NodeEntry(String label, NodeFactory<N> factory, ExtraSerializer<N> extraSerializer) {
            this.label = label;
            this.factory = factory;
            this.extraSerializer = extraSerializer;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        void invokeSerializer(Node node, JsonObject target, Gson gson) {
            if (this.extraSerializer != null) {
                ((ExtraSerializer) this.extraSerializer).serialize(node, target, gson);
            }
        }
    }

    private final Map<Class<? extends Node>, NodeEntry<?>> byClass = new LinkedHashMap<>();
    private final Map<String, NodeEntry<?>> byLabel = new LinkedHashMap<>();

    /** Simple registration — auto-serializes all registered vars. */
    public <N extends Node> NodeTypeRegistry registerSubtype(Class<N> clazz, Supplier<N> factory) {
        return this.registerSubtype(clazz, (json, gson) -> factory.get(), null);
    }

    /** Complex registration — factory reads domain object from JSON; extraSerializer writes it back. */
    public <N extends Node> NodeTypeRegistry registerSubtype(
        Class<N> clazz,
        NodeFactory<N> factory,
        ExtraSerializer<N> extraSerializer
    ) {
        String label = clazz.getSimpleName();
        NodeEntry<N> entry = new NodeEntry<>(label, factory, extraSerializer);
        this.byClass.put(clazz, entry);
        this.byLabel.put(label, entry);
        return this;
    }

    /** Registers an additional type-string alias that maps to an already-registered class (for backward compat). */
    public <N extends Node> NodeTypeRegistry registerAlias(String alias, Class<N> clazz) {
        NodeEntry<?> entry = this.byClass.get(clazz);
        if (entry != null) {
            this.byLabel.put(alias, entry);
        }
        return this;
    }

    /**
     * Serializes node type and all registered vars into target.
     * @return false (with warning) if the node class is not registered.
     */
    public boolean serialize(Node node, JsonObject target, Gson gson) {
        NodeEntry<?> entry = this.byClass.get(node.getClass());
        if (entry == null) {
            log.warn("Unknown node type during serialization: {}", node.getClass().getSimpleName());
            return false;
        }
        target.addProperty("type", entry.label);
        entry.invokeSerializer(node, target, gson);

        JsonObject inObj = new JsonObject();
        JsonObject outObj = new JsonObject();
        for (Map.Entry<String, VarInput<?>> e : node.getInputs().entrySet()) {
            this.serializeVar(e.getKey(), e.getValue(), inObj, gson);
        }
        for (Map.Entry<String, VarOutput<?>> e : node.getOutputs().entrySet()) {
            this.serializeVar(e.getKey(), e.getValue(), outObj, gson);
        }

        JsonObject vars = new JsonObject();
        vars.add("in", inObj);
        vars.add("out", outObj);
        target.add("vars", vars);

        return true;
    }

    /**
     * Deserializes a node from nodeObj.
     * @return null if the type string is unknown.
     */
    public Node deserialize(JsonObject nodeObj, Gson gson) {
        if (!nodeObj.has("type")) return null;
        String label = nodeObj.get("type").getAsString();
        NodeEntry<?> entry = this.byLabel.get(label);
        if (entry == null) return null;

        Node node = entry.factory.create(nodeObj, gson);

        if (nodeObj.has("vars")) {
            JsonObject vars = nodeObj.getAsJsonObject("vars");
            JsonObject inObj = vars.has("in") ? vars.getAsJsonObject("in") : null;
            JsonObject outObj = vars.has("out") ? vars.getAsJsonObject("out") : null;

            if (inObj != null) {
                for (Map.Entry<String, VarInput<?>> e : node.getInputs().entrySet()) {
                    if (inObj.has(e.getKey())) {
                        this.deserializeVar(e.getValue(), inObj.get(e.getKey()), gson);
                    }
                }
            }
            if (outObj != null) {
                for (Map.Entry<String, VarOutput<?>> e : node.getOutputs().entrySet()) {
                    if (outObj.has(e.getKey())) {
                        this.deserializeVar(e.getValue(), outObj.get(e.getKey()), gson);
                    }
                }
            }
        }

        return node;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void deserializeVar(Var var, JsonElement elem, Gson gson) {
        Class<?> type = var.getType();
        if (type == ExecSignal.class) return;
        try {
            if (type == Boolean.class) {
                this.setValueUnchecked(var, elem.getAsBoolean());
            } else if (Number.class.isAssignableFrom(type)) {
                this.setValueUnchecked(var, elem.getAsDouble());
            } else if (type == String.class) {
                this.setValueUnchecked(var, elem.getAsString());
            } else if (type.isEnum()) {
                this.setValueUnchecked(var, Enum.valueOf((Class<Enum>) type, elem.getAsString()));
            } else if (type == String[].class) {
                JsonArray arr = elem.getAsJsonArray();
                String[] strings = new String[arr.size()];
                for (int i = 0; i < arr.size(); i++) strings[i] = arr.get(i).getAsString();
                this.setValueUnchecked(var, strings);
            } else if (type == WorldPoint.class) {
                this.setValueUnchecked(var, gson.fromJson(elem, WorldPoint.class));
            }
            // Other types (InventoryItemDataMap, Object, etc.) are runtime-only — silently skipped
        } catch (Exception e) {
            log.warn("Failed to deserialize var '{}': {}", var.getName(), e.getMessage());
        }
    }

    private void serializeVar(String name, Var<?> var, JsonObject target, Gson gson) {
        Class<?> type = var.getType();
        Object value = var.getValue();
        if (type == ExecSignal.class || value == null) return;

        if (type == Boolean.class) {
            target.addProperty(name, (Boolean) value);
        } else if (Number.class.isAssignableFrom(type)) {
            target.addProperty(name, ((Number) value).doubleValue());
        } else if (type == String.class) {
            target.addProperty(name, (String) value);
        } else if (type.isEnum()) {
            target.addProperty(name, ((Enum<?>) value).name());
        } else if (type == String[].class) {
            JsonArray arr = new JsonArray();
            for (String s : (String[]) value) arr.add(s);
            target.add(name, arr);
        } else if (type == WorldPoint.class) {
            target.add(name, gson.toJsonTree(value));
        }
        // Other types silently skipped — restored at runtime from game state
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setValueUnchecked(Var var, Object value) {
        var.setValue(value);
    }
}
