package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class Dummy<T> extends Node {
    private final VarInput<T> input;
    private final VarOutput<T> output;

    public Dummy(Class<T> type) {
        this.input = new VarInput<>(this, "Input", type, null);
        this.output = new VarOutput<>(this, "Output", type, null);
    }

    @Override
    public void process() {
        output.setValue(input.getValue());
    }
}
