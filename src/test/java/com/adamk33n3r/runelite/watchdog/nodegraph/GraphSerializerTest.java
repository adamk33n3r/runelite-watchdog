package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.NodeTypeRegistry;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Inventory;
import com.adamk33n3r.nodegraph.nodes.constants.Location;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.constants.PluginState;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.nodegraph.nodes.flow.Counter;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.nodegraph.nodes.flow.TimerNode;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import com.adamk33n3r.nodegraph.nodes.math.*;
import com.adamk33n3r.nodegraph.nodes.utility.DisplayNode;
import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import com.adamk33n3r.runelite.watchdog.RuntimeTypeAdapterFactory;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.nodegraph.GraphSerializer;
import com.adamk33n3r.runelite.watchdog.alerts.InventoryAlert;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;

import com.google.gson.Gson;
import net.runelite.api.coords.WorldPoint;
import net.runelite.http.api.RuneLiteAPI;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphSerializerTest {

    private Gson gson;

    @Before
    public void setup() {
        RuntimeTypeAdapterFactory<Alert> alertFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .recognizeSubtypes()
            .registerSubtype(ChatAlert.class)
            .registerSubtype(AdvancedAlert.class);
        RuntimeTypeAdapterFactory<Notification> notifFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(ScreenFlash.class);
        Gson intermediateGson = RuneLiteAPI.GSON.newBuilder()
            .registerTypeAdapterFactory(alertFactory)
            .registerTypeAdapterFactory(notifFactory)
            .create();
        GraphSerializer serializer = new GraphSerializer(intermediateGson, buildRegistry(intermediateGson));
        this.gson = intermediateGson.newBuilder()
            .registerTypeAdapter(Graph.class, serializer)
            .create();
    }

    private static NodeTypeRegistry buildRegistry(Gson subGson) {
        return new NodeTypeRegistry()
            .registerSubtype(TriggerNode.class,
                (json, gson) -> new TriggerNode(subGson.fromJson(json.get("alert"), Alert.class)),
                (node, obj, gson) -> obj.add("alert", subGson.toJsonTree(node.getAlert(), Alert.class)))
            .registerSubtype(ActionNode.class,
                (json, gson) -> new ActionNode(subGson.fromJson(json.get("notification"), Notification.class)),
                (node, obj, gson) -> obj.add("notification", subGson.toJsonTree(node.getNotification(), Notification.class)))
            .registerAlias("NotificationNode", ActionNode.class)
            .registerSubtype(Add.class, Add::new)
            .registerSubtype(Subtract.class, Subtract::new)
            .registerSubtype(Multiply.class, Multiply::new)
            .registerSubtype(Divide.class, Divide::new)
            .registerSubtype(Min.class, Min::new)
            .registerSubtype(Max.class, Max::new)
            .registerSubtype(Clamp.class, Clamp::new)
            .registerSubtype(BooleanGate.class, BooleanGate::new)
            .registerSubtype(Equality.class, Equality::new)
            .registerSubtype(Bool.class, Bool::new)
            .registerSubtype(Num.class, Num::new)
            .registerSubtype(Location.class, Location::new)
            .registerSubtype(Inventory.class, Inventory::new)
            .registerSubtype(PluginState.class,
                (json, gson) -> {
                    PluginState ps = new PluginState();
                    if (json.has("pluginName")) ps.setPluginName(json.get("pluginName").getAsString());
                    return ps;
                },
                (ps, obj, gson) -> { if (ps.getPluginName() != null) obj.addProperty("pluginName", ps.getPluginName()); })
            .registerAlias("PluginVar", PluginState.class)
            .registerSubtype(InventoryCheck.class,
                (json, gson) -> {
                    InventoryCheck inv = new InventoryCheck();
                    if (json.has("inventoryAlertType")) inv.setInventoryAlertType(InventoryAlert.InventoryAlertType.valueOf(json.get("inventoryAlertType").getAsString()));
                    if (json.has("inventoryMatchType")) inv.setInventoryMatchType(InventoryAlert.InventoryMatchType.valueOf(json.get("inventoryMatchType").getAsString()));
                    if (json.has("itemName")) inv.setItemName(json.get("itemName").getAsString());
                    if (json.has("isRegexEnabled")) inv.setRegexEnabled(json.get("isRegexEnabled").getAsBoolean());
                    if (json.has("itemQuantity")) inv.setItemQuantity(json.get("itemQuantity").getAsInt());
                    if (json.has("quantityComparator")) inv.setQuantityComparator(ComparableNumber.Comparator.valueOf(json.get("quantityComparator").getAsString()));
                    return inv;
                },
                (inv, obj, gson) -> {
                    obj.addProperty("inventoryAlertType", inv.getInventoryAlertType().name());
                    obj.addProperty("inventoryMatchType", inv.getInventoryMatchType().name());
                    obj.addProperty("itemName", inv.getItemName());
                    obj.addProperty("isRegexEnabled", inv.isRegexEnabled());
                    obj.addProperty("itemQuantity", inv.getItemQuantity());
                    obj.addProperty("quantityComparator", inv.getQuantityComparator().name());
                })
            .registerAlias("InventoryVar", InventoryCheck.class)
            .registerSubtype(LocationCompare.class,
                (json, gson) -> {
                    LocationCompare lc = new LocationCompare();
                    if (json.has("distance")) lc.setDistance(json.get("distance").getAsInt());
                    if (json.has("cardinalOnly")) lc.setCardinalOnly(json.get("cardinalOnly").getAsBoolean());
                    return lc;
                },
                (lc, obj, gson) -> {
                    obj.addProperty("distance", lc.getDistance());
                    obj.addProperty("cardinalOnly", lc.isCardinalOnly());
                })
            .registerSubtype(DelayNode.class, DelayNode::new)
            .registerAlias("Delay", DelayNode.class)
            .registerSubtype(com.adamk33n3r.nodegraph.nodes.flow.Counter.class, com.adamk33n3r.nodegraph.nodes.flow.Counter::new)
            .registerSubtype(TimerNode.class, TimerNode::new)
            .registerAlias("Timer", TimerNode.class)
            .registerSubtype(Branch.class, Branch::new)
            .registerSubtype(DisplayNode.class, DisplayNode::new)
            .registerSubtype(NoteNode.class,
                (json, gson) -> {
                    NoteNode n = new NoteNode();
                    if (json.has("note")) n.setNote(json.get("note").getAsString());
                    return n;
                },
                (note, obj, gson) -> obj.addProperty("note", note.getNote()));
    }

    @Test
    public void roundTrip_emptyGraph() {
        Graph graph = new Graph();
        Graph loaded = roundTrip(graph);
        assertTrue(loaded.getNodes().isEmpty());
        assertTrue(loaded.getConnections().isEmpty());
    }

    @Test
    public void roundTrip_singleTriggerNode() {
        Graph graph = new Graph();
        ChatAlert alert = new ChatAlert("harvest");
        alert.setMessage("*ready to harvest*");
        graph.add(new TriggerNode(alert));

        Graph loaded = roundTrip(graph);

        assertEquals(1, loaded.getNodes().size());
        assertTrue(loaded.getNodes().get(0) instanceof TriggerNode);
        TriggerNode loadedTrigger = (TriggerNode) loaded.getNodes().get(0);
        assertEquals("harvest", loadedTrigger.getAlert().getName());
    }

    @Test
    public void roundTrip_preservesConnection() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        ScreenFlash notif = new ScreenFlash();
        ActionNode notifNode = new ActionNode(notif);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        Graph loaded = roundTrip(graph);

        assertEquals(2, loaded.getNodes().size());
        assertEquals(1, loaded.getConnections().size());
        Connection<?> conn = loaded.getConnections().get(0);
        assertEquals("Exec", conn.getOutput().getName());
        assertEquals("Exec", conn.getInput().getName());
    }

    @Test
    public void roundTrip_preservesNodePositions() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        trigger.setX(150);
        trigger.setY(250);
        graph.add(trigger);

        Graph loaded = roundTrip(graph);
        assertEquals(150, loaded.getNodes().get(0).getX());
        assertEquals(250, loaded.getNodes().get(0).getY());
    }

    @Test
    public void roundTrip_preservesNumValue() {
        Graph graph = new Graph();
        Num num = new Num();
        num.setValue(42);
        graph.add(num);

        Graph loaded = roundTrip(graph);
        Num loadedNum = (Num) loaded.getNodes().get(0);
        assertEquals(42, loadedNum.getValue().getValue().intValue());
    }

    @Test
    public void roundTrip_preservesBoolValue() {
        Graph graph = new Graph();
        Bool bool = new Bool();
        bool.setValue(false);
        graph.add(bool);

        Graph loaded = roundTrip(graph);
        Bool loadedBool = (Bool) loaded.getNodes().get(0);
        assertFalse(loadedBool.getValueOut().getValue());
    }

    @Test
    public void roundTrip_varsReRegisteredAfterLoad() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        graph.add(trigger);

        Graph loaded = roundTrip(graph);
        TriggerNode loadedTrigger = (TriggerNode) loaded.getNodes().get(0);
        assertTrue(loadedTrigger.getInputs().containsKey("Capture Groups In"));
        assertTrue(loadedTrigger.getOutputs().containsKey("Exec"));
    }

    @Test
    public void roundTrip_valuePropagatesAfterLoad() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        ScreenFlash notif = new ScreenFlash();
        ActionNode notifNode = new ActionNode(notif);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        Graph loaded = roundTrip(graph);
        TriggerNode loadedTrigger = (TriggerNode) loaded.getNodes().stream()
            .filter(n -> n instanceof TriggerNode).findFirst().get();
        ActionNode loadedNotif = (ActionNode) loaded.getNodes().stream()
            .filter(n -> n instanceof ActionNode).findFirst().get();

        String[] groups = new String[]{"hello"};
        loadedTrigger.getCaptureGroupsIn().setValue(groups);
        loadedTrigger.process();

        assertArrayEquals(groups, loadedNotif.getExec().getValue().getCaptureGroups());
    }

    @Test
    public void roundTrip_multipleNodes_allPreserved() {
        Graph graph = new Graph();
        graph.add(new TriggerNode(new ChatAlert("trigger1")));
        graph.add(new TriggerNode(new ChatAlert("trigger2")));
        graph.add(new ActionNode(new ScreenFlash()));
        graph.add(new Num());
        graph.add(new Bool());

        Graph loaded = roundTrip(graph);
        assertEquals(5, loaded.getNodes().size());
    }

    @Test
    public void roundTrip_preservesPluginVarConfig() {
        Graph graph = new Graph();
        PluginState pv = new PluginState();
        pv.setPluginName("GPU");
        graph.add(pv);

        Graph loaded = roundTrip(graph);
        PluginState loadedPv = (PluginState) loaded.getNodes().get(0);
        assertEquals("GPU", loadedPv.getPluginName());
    }

    @Test
    public void roundTrip_preservesInventoryVarConfig() {
        Graph graph = new Graph();
        InventoryCheck inv = new InventoryCheck();
        inv.setInventoryAlertType(InventoryAlert.InventoryAlertType.ITEM);
        inv.setInventoryMatchType(InventoryAlert.InventoryMatchType.UN_NOTED);
        inv.setItemName("Lobster");
        inv.setRegexEnabled(true);
        inv.setItemQuantity(5);
        inv.setQuantityComparator(ComparableNumber.Comparator.GREATER_THAN);
        graph.add(inv);

        Graph loaded = roundTrip(graph);
        InventoryCheck loadedInv = (InventoryCheck) loaded.getNodes().get(0);
        assertEquals(InventoryAlert.InventoryAlertType.ITEM, loadedInv.getInventoryAlertType());
        assertEquals(InventoryAlert.InventoryMatchType.UN_NOTED, loadedInv.getInventoryMatchType());
        assertEquals("Lobster", loadedInv.getItemName());
        assertTrue(loadedInv.isRegexEnabled());
        assertEquals(5, loadedInv.getItemQuantity());
        assertEquals(ComparableNumber.Comparator.GREATER_THAN, loadedInv.getQuantityComparator());
    }

    @Test
    public void roundTrip_preservesLocationCompareConfig() {
        Graph graph = new Graph();
        LocationCompare lc = new LocationCompare();
        lc.getA().setValue(new WorldPoint(3200, 3200, 0));
        lc.getB().setValue(new WorldPoint(3210, 3210, 1));
        lc.setDistance(5);
        lc.setCardinalOnly(true);
        graph.add(lc);

        Graph loaded = roundTrip(graph);
        System.out.println(loaded.getNodes().size());
        LocationCompare loadedLc = (LocationCompare) loaded.getNodes().get(0);
        assertEquals(new WorldPoint(3200, 3200, 0), loadedLc.getA().getValue());
        assertEquals(new WorldPoint(3210, 3210, 1), loadedLc.getB().getValue());
        assertEquals(5, loadedLc.getDistance());
        assertTrue(loadedLc.isCardinalOnly());
    }

    @Test
    public void roundTrip_delayNode_preservesDelayMs() {
        Graph graph = new Graph();
        DelayNode delay = new DelayNode();
        delay.getDelayMs().setValue(750);
        graph.add(delay);

        Graph loaded = roundTrip(graph);

        assertEquals(1, loaded.getNodes().size());
        assertTrue(loaded.getNodes().get(0) instanceof DelayNode);
        DelayNode loadedDelay = (DelayNode) loaded.getNodes().get(0);
        assertEquals(750, loadedDelay.getDelayMs().getValue().intValue());
    }

    @Test
    public void roundTrip_delayNode_withConnections() {
        Graph graph = new Graph();
        TriggerNode trigger = new TriggerNode(new ChatAlert("test"));
        DelayNode delay = new DelayNode();
        delay.getDelayMs().setValue(250);
        ScreenFlash notif = new ScreenFlash();
        ActionNode action = new ActionNode(notif);

        graph.add(trigger);
        graph.add(delay);
        graph.add(action);
        graph.connect(trigger.getExec(), delay.getExec());
        graph.connect(delay.getExecOut(), action.getExec());

        Graph loaded = roundTrip(graph);

        assertEquals(3, loaded.getNodes().size());
        assertEquals(2, loaded.getConnections().size());
        long delayCount = loaded.getNodes().stream().filter(n -> n instanceof DelayNode).count();
        assertEquals(1, delayCount);
    }

    @Test
    public void roundTrip_counterNode_preservesValue() {
        Graph graph = new Graph();
        Counter counter = new Counter();
        counter.initValue(42);
        graph.add(counter);

        Graph loaded = roundTrip(graph);

        assertEquals(1, loaded.getNodes().size());
        assertTrue(loaded.getNodes().get(0) instanceof Counter);
        Counter loadedCounter = (Counter) loaded.getNodes().get(0);
        assertEquals(42, loadedCounter.getCount().getValue().intValue());
    }

    @Test
    public void roundTrip_locationNode_preservesWorldPoint() {
        Graph graph = new Graph();
        Location loc = new Location();
        loc.setValue(new WorldPoint(3200, 3200, 0));
        graph.add(loc);

        Graph loaded = roundTrip(graph);

        assertEquals(1, loaded.getNodes().size());
        assertTrue(loaded.getNodes().get(0) instanceof Location);
        Location loadedLoc = (Location) loaded.getNodes().get(0);
        assertEquals(new WorldPoint(3200, 3200, 0), loadedLoc.getValue().getValue());
    }

    private Graph roundTrip(Graph graph) {
        String json = gson.toJson(graph, Graph.class);
        System.out.println("roundTrip: " + json);
        return gson.fromJson(json, Graph.class);
    }
}
