package com.adamk33n3r.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GraphSerializer implements JsonSerializer<Graph>, JsonDeserializer<Graph> {
    private final Gson subGson;
    private final NodeTypeRegistry nodeRegistry;

    public GraphSerializer(Gson gson, NodeTypeRegistry nodeRegistry) {
        this.subGson = gson;
        this.nodeRegistry = nodeRegistry;
    }

    @Override
    public JsonElement serialize(Graph graph, Type type, JsonSerializationContext ctx) {
        JsonObject root = new JsonObject();

        JsonArray nodesArr = new JsonArray();
        for (Node node : graph.getNodes()) {
            JsonObject nodeObj = new JsonObject();
            nodeObj.addProperty("id", node.getId().toString());
            nodeObj.addProperty("x", node.getX());
            nodeObj.addProperty("y", node.getY());

            // Lossless resave for unknown-type placeholders
            if (node instanceof NoteNode) {
                NoteNode noteNode = (NoteNode) node;
                if (noteNode.getOriginalType() != null && noteNode.getOriginalJson() != null) {
                    for (Map.Entry<String, JsonElement> entry : noteNode.getOriginalJson().entrySet()) {
                        nodeObj.add(entry.getKey(), entry.getValue());
                    }
                    nodesArr.add(nodeObj);
                    continue;
                }
            }

            if (!this.nodeRegistry.serialize(node, nodeObj, this.subGson)) continue;
            nodesArr.add(nodeObj);
        }
        root.add("nodes", nodesArr);

        JsonArray connectionsArr = new JsonArray();
        for (Connection<?> conn : graph.getConnections()) {
            JsonObject connObj = new JsonObject();
            connObj.addProperty("fromNode", conn.getOutput().getNode().getId().toString());
            connObj.addProperty("fromVar", conn.getOutput().getName());
            connObj.addProperty("toNode", conn.getInput().getNode().getId().toString());
            connObj.addProperty("toVar", conn.getInput().getName());
            connectionsArr.add(connObj);
        }
        root.add("connections", connectionsArr);

        return root;
    }

    @Override
    public Graph deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        Graph graph = new Graph();
        Map<String, Node> nodeById = new HashMap<>();

        JsonArray nodesArr = root.getAsJsonArray("nodes");
        if (nodesArr != null) {
            for (JsonElement nodeElem : nodesArr) {
                JsonObject nodeObj = nodeElem.getAsJsonObject();
                String id = nodeObj.has("id") ? nodeObj.get("id").getAsString() : null;
                int x = nodeObj.has("x") ? nodeObj.get("x").getAsInt() : 0;
                int y = nodeObj.has("y") ? nodeObj.get("y").getAsInt() : 0;

                Node node;
                try {
                    node = this.nodeRegistry.deserialize(nodeObj, this.subGson);
                    if (node == null) {
                        String nodeType = nodeObj.has("type") ? nodeObj.get("type").getAsString() : "unknown";
                        log.warn("Unknown node type during deserialization, creating placeholder: {}", nodeType);
                        NoteNode placeholder = new NoteNode();
                        placeholder.setOriginalType(nodeType);
                        placeholder.setOriginalJson(nodeObj.deepCopy());
                        node = placeholder;
                    }
                } catch (Exception e) {
                    String nodeType = nodeObj.has("type") ? nodeObj.get("type").getAsString() : "unknown";
                    log.warn("Failed to deserialize node of type {}: {}", nodeType, e.getMessage());
                    continue;
                }

                node.setX(x);
                node.setY(y);
                if (id != null) {
                    node.setId(UUID.fromString(id));
                    nodeById.put(id, node);
                }
                graph.add(node);
            }
        }

        JsonArray connectionsArr = root.getAsJsonArray("connections");
        if (connectionsArr != null) {
            for (JsonElement connElem : connectionsArr) {
                JsonObject connObj = connElem.getAsJsonObject();
                String fromNodeId = connObj.get("fromNode").getAsString();
                String fromVar = connObj.get("fromVar").getAsString();
                String toNodeId = connObj.get("toNode").getAsString();
                String toVar = connObj.get("toVar").getAsString();

                Node fromNode = nodeById.get(fromNodeId);
                Node toNode = nodeById.get(toNodeId);
                if (fromNode == null || toNode == null) {
                    log.warn("Could not find nodes for connection: {} -> {}", fromNodeId, toNodeId);
                    continue;
                }

                VarOutput<?> output = fromNode.getOutputs().get(fromVar);
                VarInput<?> input = toNode.getInputs().get(toVar);
                if (output == null || input == null) {
                    log.warn("Could not find vars for connection: {}.{} -> {}.{}", fromNodeId, fromVar, toNodeId, toVar);
                    continue;
                }

                this.connectUnchecked(graph, output, input);
            }
        }

        return graph;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void connectUnchecked(Graph graph, VarOutput<?> output, VarInput<?> input) {
        graph.connect((VarOutput) output, (VarInput) input);
    }
}
