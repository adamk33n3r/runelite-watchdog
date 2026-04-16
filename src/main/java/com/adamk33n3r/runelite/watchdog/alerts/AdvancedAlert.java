package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdvancedAlert extends Alert {
    private Graph graph = new Graph();

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
        this.graph.getReachableActionsFromTrigger(triggerNode)
            .forEach(ActionNode::fire);
    }
}
