package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import lombok.Getter;

@Getter
public class ConnectionPointIn<T> extends ConnectionPoint {
    private final VarInput<T> inputVar;

    public ConnectionPointIn(NodePanel nodePanel, VarInput<T> varInput) {
        super(nodePanel);
        this.inputVar = varInput;
    }
}
