package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.Node;
import lombok.Getter;

/**
 * Abstract base for variable nodes (Inventory, Location, and future variable nodes).
 */
@Getter
public abstract class VariableNode extends Node {
    protected VariableNode() {
    }
}
