package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

/**
 * Abstract base for user-named variable nodes (Bool, Num, and future condition nodes).
 * Provides a persisted nameOut output that stores the user-assigned display name.
 */
@Getter
public abstract class VariableNode extends Node {
    private final VarOutput<String> nameOut = new VarOutput<>(this, "Name", String.class, "");

    protected VariableNode() {
        reg(this.nameOut);
    }
}
