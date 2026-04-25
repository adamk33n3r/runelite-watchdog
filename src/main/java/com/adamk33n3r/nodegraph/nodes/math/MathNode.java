package com.adamk33n3r.nodegraph.nodes.math;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

/**
 * Abstract base for math operation nodes.
 * Provides a shared {@code result} output so all math nodes expose a uniform Number output.
 */
@Getter
public abstract class MathNode extends Node {
    private final VarOutput<Number> result = new VarOutput<>(this, "Result", Number.class, 0d);

    protected MathNode() {
        reg(this.result);
    }
}
