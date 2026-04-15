package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.*;
import com.adamk33n3r.nodegraph.nodes.*;
import com.adamk33n3r.nodegraph.nodes.constants.*;
import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import com.adamk33n3r.runelite.watchdog.RuntimeTypeAdapterFactory;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;

import net.runelite.api.coords.WorldPoint;

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
            } else if (node instanceof BooleanGate) {
                nodeObj.addProperty("type", "BooleanGate");
                nodeObj.addProperty("op", ((BooleanGate) node).getOp().getValue().name());
                nodeObj.addProperty("a", ((BooleanGate) node).getA().getValue());
                nodeObj.addProperty("b", ((BooleanGate) node).getB().getValue());
            } else if (node instanceof Equality) {
                nodeObj.addProperty("type", "Equality");
                nodeObj.addProperty("op", ((Equality) node).getOp().getValue().name());
                nodeObj.addProperty("a", ((Equality) node).getA().getValue());
                nodeObj.addProperty("b", ((Equality) node).getB().getValue());
            } else if (node instanceof Location) {
                nodeObj.addProperty("type", "Location");
            } else if (node instanceof PluginVar) {
                nodeObj.addProperty("type", "PluginVar");
                nodeObj.addProperty("pluginName", ((PluginVar) node).getPluginName());
            } else if (node instanceof Inventory) {
                nodeObj.addProperty("type", "Inventory");
            } else if (node instanceof InventoryCheck) {
                InventoryCheck inv = (InventoryCheck) node;
                nodeObj.addProperty("type", "InventoryVar");
                nodeObj.addProperty("name", inv.getNameOut().getValue());
                nodeObj.addProperty("inventoryAlertType", inv.getInventoryAlertType().name());
                nodeObj.addProperty("inventoryMatchType", inv.getInventoryMatchType().name());
                nodeObj.addProperty("itemName", inv.getItemName());
                nodeObj.addProperty("isRegexEnabled", inv.isRegexEnabled());
                nodeObj.addProperty("itemQuantity", inv.getItemQuantity());
                nodeObj.addProperty("quantityComparator", inv.getQuantityComparator().name());
            } else if (node instanceof LocationCompare) {
                LocationCompare lc = (LocationCompare) node;
                nodeObj.addProperty("type", "LocationCompare");
                WorldPoint a = lc.getA().getValue();
                nodeObj.addProperty("ax", a.getX());
                nodeObj.addProperty("ay", a.getY());
                nodeObj.addProperty("aPlane", a.getPlane());
                WorldPoint b = lc.getB().getValue();
                nodeObj.addProperty("bx", b.getX());
                nodeObj.addProperty("by", b.getY());
                nodeObj.addProperty("bPlane", b.getPlane());
                nodeObj.addProperty("distance", lc.getDistance());
                nodeObj.addProperty("cardinalOnly", lc.isCardinalOnly());
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
                        case "BooleanGate": {
                            BooleanGate gate = new BooleanGate();
                            if (nodeObj.has("op")) {
                                gate.getOp().setValue(BooleanGate.Op.valueOf(nodeObj.get("op").getAsString()));
                                gate.getA().setValue(nodeObj.get("a").getAsBoolean());
                                gate.getB().setValue(nodeObj.get("b").getAsBoolean());
                            }
                            node = gate;
                            break;
                        }
                        case "Equality": {
                            Equality eq = new Equality();
                            if (nodeObj.has("op")) {
                                eq.getOp().setValue(Equality.Op.valueOf(nodeObj.get("op").getAsString()));
                                eq.getA().setValue(nodeObj.get("a").getAsDouble());
                                eq.getB().setValue(nodeObj.get("b").getAsDouble());
                            }
                            node = eq;
                            break;
                        }
                        case "Location": {
                            Location loc = new Location();
                            if (nodeObj.has("name")) {
                                loc.getNameOut().setValue(nodeObj.get("name").getAsString());
                            }
                            node = loc;
                            break;
                        }
                        case "PluginVar": {
                            PluginVar pv = new PluginVar();
                            if (nodeObj.has("name")) {
                                pv.getNameOut().setValue(nodeObj.get("name").getAsString());
                            }
                            if (nodeObj.has("pluginName")) {
                                pv.setPluginName(nodeObj.get("pluginName").getAsString());
                            }
                            node = pv;
                            break;
                        }
                        case "Inventory": {
                            node = new Inventory();
                            break;
                        }
                        case "InventoryVar": {
                            InventoryCheck inv = new InventoryCheck();
                            if (nodeObj.has("name")) {
                                inv.getNameOut().setValue(nodeObj.get("name").getAsString());
                            }
                            if (nodeObj.has("inventoryAlertType")) {
                                inv.setInventoryAlertType(InventoryAlert.InventoryAlertType.valueOf(nodeObj.get("inventoryAlertType").getAsString()));
                            }
                            if (nodeObj.has("inventoryMatchType")) {
                                inv.setInventoryMatchType(InventoryAlert.InventoryMatchType.valueOf(nodeObj.get("inventoryMatchType").getAsString()));
                            }
                            if (nodeObj.has("itemName")) {
                                inv.setItemName(nodeObj.get("itemName").getAsString());
                            }
                            if (nodeObj.has("isRegexEnabled")) {
                                inv.setRegexEnabled(nodeObj.get("isRegexEnabled").getAsBoolean());
                            }
                            if (nodeObj.has("itemQuantity")) {
                                inv.setItemQuantity(nodeObj.get("itemQuantity").getAsInt());
                            }
                            if (nodeObj.has("quantityComparator")) {
                                inv.setQuantityComparator(ComparableNumber.Comparator.valueOf(nodeObj.get("quantityComparator").getAsString()));
                            }
                            node = inv;
                            break;
                        }
                        case "LocationCompare": {
                            LocationCompare lc = new LocationCompare();
                            if (nodeObj.has("ax")) {
                                lc.getA().setValue(new WorldPoint(
                                    nodeObj.get("ax").getAsInt(),
                                    nodeObj.get("ay").getAsInt(),
                                    nodeObj.get("aPlane").getAsInt()));
                            }
                            if (nodeObj.has("bx")) {
                                lc.getB().setValue(new WorldPoint(
                                    nodeObj.get("bx").getAsInt(),
                                    nodeObj.get("by").getAsInt(),
                                    nodeObj.get("bPlane").getAsInt()));
                            }
                            if (nodeObj.has("distance")) {
                                lc.setDistance(nodeObj.get("distance").getAsInt());
                            }
                            if (nodeObj.has("cardinalOnly")) {
                                lc.setCardinalOnly(nodeObj.get("cardinalOnly").getAsBoolean());
                            }
                            node = lc;
                            break;
                        }
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
