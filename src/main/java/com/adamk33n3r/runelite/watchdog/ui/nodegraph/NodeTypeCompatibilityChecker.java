package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;

import javax.inject.Inject;

/**
 * Checks whether a node-type enum entry has at least one {@code VarInput} compatible with a given drag type.
 * <p>
 * Uses {@link NodeProbeFactory} to instantiate a lightweight probe node, then inspects
 * {@link Node#getInputs()} — no hardcoded type lists. Adding a new {@code VarInput} to any
 * node class automatically updates compatibility filtering without changing this class.
 */
public class NodeTypeCompatibilityChecker {
    @Inject
    private NodeProbeFactory nodeProbeFactory;

    public boolean hasCompatibleInput(Enum<?> nodeType, Class<?> dragType) {
        Node probe = this.nodeProbeFactory.create(nodeType);
        if (probe == null) return false;
        return probe.getInputs().values().stream()
            .anyMatch(input -> input.getType() == dragType || input.getType() == Object.class);
    }
}
