package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.*;
import com.adamk33n3r.nodegraph.nodes.*;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import com.adamk33n3r.runelite.watchdog.RuntimeTypeAdapterFactory;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GraphSerializer implements JsonSerializer<Graph>, JsonDeserializer<Graph> {
    private final Gson subGson;

    public GraphSerializer(
        RuntimeTypeAdapterFactory<Alert> alertFactory,
        RuntimeTypeAdapterFactory<Notification> notificationFactory,
        Gson baseGson
    ) {
        this.subGson = baseGson.newBuilder()
            .registerTypeAdapterFactory(alertFactory)
            .registerTypeAdapterFactory(notificationFactory)
            .create();
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

            if (node instanceof ContinuousTriggerNode) {
                nodeObj.addProperty("type", "ContinuousTriggerNode");
                nodeObj.add("alert", subGson.toJsonTree(((TriggerNode) node).getAlert(), Alert.class));
            } else if (node instanceof TriggerNode) {
                nodeObj.addProperty("type", "TriggerNode");
                nodeObj.add("alert", subGson.toJsonTree(((TriggerNode) node).getAlert(), Alert.class));
            } else if (node instanceof NotificationNode) {
                nodeObj.addProperty("type", "NotificationNode");
                nodeObj.add("notification", subGson.toJsonTree(((NotificationNode) node).getNotification(), Notification.class));
            } else if (node instanceof Num) {
                nodeObj.addProperty("type", "Num");
                nodeObj.addProperty("value", ((Num) node).getValue().getValue().doubleValue());
                nodeObj.addProperty("name", ((Num) node).getNameOut().getValue());
            } else if (node instanceof Bool) {
                nodeObj.addProperty("type", "Bool");
                nodeObj.addProperty("value", ((Bool) node).getValueOut().getValue());
                nodeObj.addProperty("name", ((Bool) node).getNameOut().getValue());
            } else if (node instanceof Add) {
                nodeObj.addProperty("type", "Add");
            } else {
                log.warn("Unknown node type during serialization: {}", node.getClass().getSimpleName());
                continue;
            }
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
                String nodeType = nodeObj.get("type").getAsString();
                String id = nodeObj.get("id").getAsString();
                int x = nodeObj.has("x") ? nodeObj.get("x").getAsInt() : 0;
                int y = nodeObj.has("y") ? nodeObj.get("y").getAsInt() : 0;

                Node node = null;
                try {
                    switch (nodeType) {
                        case "TriggerNode": {
                            Alert alert = subGson.fromJson(nodeObj.get("alert"), Alert.class);
                            node = new TriggerNode(alert);
                            break;
                        }
                        case "ContinuousTriggerNode": {
                            ContinuousAlert alert = (ContinuousAlert) subGson.fromJson(nodeObj.get("alert"), Alert.class);
                            node = new ContinuousTriggerNode(alert);
                            break;
                        }
                        case "NotificationNode": {
                            Notification notif = subGson.fromJson(nodeObj.get("notification"), Notification.class);
                            node = new NotificationNode(notif);
                            break;
                        }
                        case "Num": {
                            Num num = new Num();
                            if (nodeObj.has("value")) {
                                num.setValue(nodeObj.get("value").getAsInt());
                            }
                            if (nodeObj.has("name")) {
                                num.getNameOut().setValue(nodeObj.get("name").getAsString());
                            }
                            node = num;
                            break;
                        }
                        case "Bool": {
                            Bool bool = new Bool();
                            if (nodeObj.has("value")) {
                                bool.setValue(nodeObj.get("value").getAsBoolean());
                            }
                            if (nodeObj.has("name")) {
                                bool.getNameOut().setValue(nodeObj.get("name").getAsString());
                            }
                            node = bool;
                            break;
                        }
                        case "Add":
                            node = new Add();
                            break;
                        default:
                            log.warn("Unknown node type during deserialization: {}", nodeType);
                            continue;
                    }
                } catch (Exception e) {
                    log.warn("Failed to deserialize node of type {}: {}", nodeType, e.getMessage());
                    continue;
                }

                node.setX(x);
                node.setY(y);
                node.setId(UUID.fromString(id));
                nodeById.put(id, node);
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

                connectUnchecked(graph, output, input);
            }
        }

        return graph;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void connectUnchecked(Graph graph, VarOutput<?> output, VarInput<?> input) {
        graph.connect((VarOutput) output, (VarInput) input);
    }
}
