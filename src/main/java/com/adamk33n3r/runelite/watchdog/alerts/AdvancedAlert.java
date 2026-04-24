package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;

@Getter
@Setter
@Accessors(chain = true)
public class AdvancedAlert extends Alert {
    private Graph graph = new Graph();
    private transient BiConsumer<AdvancedAlert, Throwable> errorHandler;

    public AdvancedAlert() {
        this("New Advanced Alert");
    }

    public AdvancedAlert(String name) {
        super(name);
        // So that we don't serialize these
        this.setNotifications(null);
        this.setAlertMode(null);
    }

    public void addWelcomeNote() {
        NoteNode note = new NoteNode();
        note.setX(20);
        note.setY(20);
        note.getNote().setValue(
            "Welcome to the Graph Editor!\n\n" +
            "Quick Start:\n" +
            "  \u2022 Right-click the canvas to add a node\n" +
            "  \u2022 Drag from an output port to an input\n" +
            "    port to connect them\n" +
            "  \u2022 Start with a Trigger node \u2014 it fires\n" +
            "    when your alert condition is met\n\n" +
            "Port Types:\n" +
            "  \u25b7 Triangle = execution flow\n" +
            "    (controls WHEN things happen)\n" +
            "  \u25cf Circle = data flow\n" +
            "    (controls WHAT values are passed)\n\n" +
            "Colors: Cyan=Number  Green=Boolean\n" +
            "        Orange=String  Yellow=Location\n\n" +
            "Delete this note when you're ready!"
        );
        this.graph.add(note);
    }

    /**
     * Fires the graph starting from the given trigger node with the provided capture group values.
     * Propagates through the graph and fires all reachable enabled notification nodes.
     */
    public void fireTriggerNode(TriggerNode triggerNode, String[] triggerValues) {
        triggerNode.getCaptureGroupsIn().setValue(triggerValues);
        this.graph.process(triggerNode);
        if (this.errorHandler != null) {
            this.graph.setOnError(t -> this.errorHandler.accept(this, t));
        }
        Thread t = new Thread(() -> this.graph.executeExecChain(triggerNode, triggerValues));
        t.setDaemon(true);
        t.start();
    }
}
