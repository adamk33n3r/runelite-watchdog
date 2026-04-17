package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
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
