package com.adamk33n3r.nodegraph.nodes;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Logger<T> extends Node {
    private final VarInput<T> input;

    public Logger(Class<T> type) {
        this.input = new VarInput<>(this, "Input", type, null);
    }

    @Override
    public void process() {
        log.info("{}", input.getValue());
    }
}
