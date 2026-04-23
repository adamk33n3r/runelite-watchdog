package com.adamk33n3r.runelite.watchdog.nodegraph;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.NodeTypeRegistry;
import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import com.adamk33n3r.runelite.watchdog.RuntimeTypeAdapterFactory;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AdvancedAlert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.nodegraph.GraphSerializer;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;

import com.google.gson.Gson;
import net.runelite.http.api.RuneLiteAPI;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoteNodeTest {

    private Gson gson;

    @Before
    public void setup() {
        RuntimeTypeAdapterFactory<Alert> alertFactory = RuntimeTypeAdapterFactory.of(Alert.class)
            .recognizeSubtypes()
            .registerSubtype(ChatAlert.class)
            .registerSubtype(AdvancedAlert.class);
        RuntimeTypeAdapterFactory<Notification> notifFactory = RuntimeTypeAdapterFactory.of(Notification.class)
            .registerSubtype(ScreenFlash.class);
        NodeTypeRegistry registry = new NodeTypeRegistry()
            .registerSubtype(NoteNode.class,
                (json, gson) -> {
                    NoteNode n = new NoteNode();
                    if (json.has("note")) n.setNote(json.get("note").getAsString());
                    return n;
                },
                (note, obj, gson) -> obj.addProperty("note", note.getNote()));
        Gson intermediateGson = RuneLiteAPI.GSON.newBuilder()
            .registerTypeAdapterFactory(alertFactory)
            .registerTypeAdapterFactory(notifFactory)
            .create();
        GraphSerializer serializer = new GraphSerializer(intermediateGson, registry);
        this.gson = intermediateGson.newBuilder()
            .registerTypeAdapter(Graph.class, serializer)
            .create();
    }

    @Test
    public void noteNode_preservesText_afterRoundTrip() {
        Graph graph = new Graph();
        NoteNode note = new NoteNode();
        note.setNote("Hello World");
        graph.add(note);

        Graph loaded = roundTrip(graph);
        NoteNode loadedNote = (NoteNode) loaded.getNodes().get(0);
        assertEquals("Hello World", loadedNote.getNote());
    }

    @Test
    public void noteNode_withNoText_serializesCleanly() {
        Graph graph = new Graph();
        NoteNode note = new NoteNode();
        graph.add(note);

        Graph loaded = roundTrip(graph);
        assertEquals(1, loaded.getNodes().size());
        assertTrue(loaded.getNodes().get(0) instanceof NoteNode);
    }

    @Test
    public void graphSerializer_unknownNodeType_deserializesAsNoteNode() {
        String json = "{\"nodes\":[{\"id\":\"00000000-0000-0000-0000-000000000001\",\"x\":0,\"y\":0,\"type\":\"NotAThingNode\"}],\"connections\":[]}";
        Graph loaded = gson.fromJson(json, Graph.class);
        assertEquals(1, loaded.getNodes().size());
        assertTrue(loaded.getNodes().get(0) instanceof NoteNode);
        NoteNode note = (NoteNode) loaded.getNodes().get(0);
        assertEquals("NotAThingNode", note.getOriginalType());
    }

    @Test
    public void graphSerializer_unknownNodeType_survivesSaveAfterLoad() {
        String original = "{\"nodes\":[{\"id\":\"00000000-0000-0000-0000-000000000001\",\"x\":10,\"y\":20,\"type\":\"NotAThingNode\",\"someData\":42}],\"connections\":[]}";
        Graph loaded = gson.fromJson(original, Graph.class);
        String resaved = gson.toJson(loaded, Graph.class);
        // Re-serialize and deserialize again — node must still be a NoteNode with original type
        Graph reloaded = gson.fromJson(resaved, Graph.class);
        assertEquals(1, reloaded.getNodes().size());
        NoteNode note = (NoteNode) reloaded.getNodes().get(0);
        assertEquals("NotAThingNode", note.getOriginalType());
    }

    private Graph roundTrip(Graph graph) {
        String json = gson.toJson(graph, Graph.class);
        return gson.fromJson(json, Graph.class);
    }
}
