package com.adamk33n3r.runelite.watchdog.nodegraph.nodes;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Logger<T> extends Node {
    private final VarInput<T> input;

    public Logger(Class<T> type) {
        this.input = new VarInput<>(this, "Input", type, null);
        this.inputs.add(input);
    }

    @Override
    public void process() {
        log.info("{}", input.getValue());
    }
}
