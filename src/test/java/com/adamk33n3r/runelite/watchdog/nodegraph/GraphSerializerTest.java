package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Connection;
import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.RuntimeTypeAdapterFactory;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.GraphSerializer;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;

import com.google.gson.Gson;
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
        GraphSerializer serializer = new GraphSerializer(alertFactory, notifFactory, RuneLiteAPI.GSON);
        this.gson = RuneLiteAPI.GSON.newBuilder()
            .registerTypeAdapterFactory(alertFactory)
            .registerTypeAdapterFactory(notifFactory)
            .registerTypeAdapter(Graph.class, serializer)
            .create();
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
        NotificationNode notifNode = new NotificationNode(notif);
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
        NotificationNode notifNode = new NotificationNode(notif);
        graph.add(trigger);
        graph.add(notifNode);
        graph.connect(trigger.getExec(), notifNode.getExec());

        Graph loaded = roundTrip(graph);
        TriggerNode loadedTrigger = (TriggerNode) loaded.getNodes().stream()
            .filter(n -> n instanceof TriggerNode).findFirst().get();
        NotificationNode loadedNotif = (NotificationNode) loaded.getNodes().stream()
            .filter(n -> n instanceof NotificationNode).findFirst().get();

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
        graph.add(new NotificationNode(new ScreenFlash()));
        graph.add(new Num());
        graph.add(new Bool());

        Graph loaded = roundTrip(graph);
        assertEquals(5, loaded.getNodes().size());
    }

    private Graph roundTrip(Graph graph) {
        String json = gson.toJson(graph, Graph.class);
        return gson.fromJson(json, Graph.class);
    }
}
