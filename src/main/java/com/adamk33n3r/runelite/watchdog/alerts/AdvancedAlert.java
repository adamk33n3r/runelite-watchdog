package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import lombok.Getter;

public class AdvancedAlert extends Alert {
    @Getter
    private Graph graph = new Graph();

    public AdvancedAlert() {
        super("New Advanced Alert");
    }

    public AdvancedAlert(String name) {
        super(name);
    }

    /**
     * Fires the graph starting from the given trigger node with the provided capture group values.
     * Propagates through the graph and fires all reachable enabled notification nodes.
     */
    public void fireTriggerNode(TriggerNode triggerNode, String[] triggerValues) {
        triggerNode.getCaptureGroupsIn().setValue(triggerValues);
        triggerNode.process();
        graph.getReachableNotificationsFromTrigger(triggerNode)
            .forEach(NotificationNode::fire);
    }
}
