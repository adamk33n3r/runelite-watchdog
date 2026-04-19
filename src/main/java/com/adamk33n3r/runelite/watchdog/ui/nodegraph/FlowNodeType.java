package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.nodegraph.nodes.flow.Counter;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.runelite.watchdog.Displayable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlowNodeType implements Displayable {
    DELAY("Delay", "Delay execution by N milliseconds", DelayNode.class, DelayNodePanel.class),
    BRANCH("Branch", "Route execution based on a boolean condition", Branch.class, BranchNodePanel.class),
    COUNTER("Counter", "Increment or reset an integer counter", Counter.class, CounterNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
