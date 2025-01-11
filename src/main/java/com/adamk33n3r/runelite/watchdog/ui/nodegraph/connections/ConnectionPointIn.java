package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import lombok.Getter;

@Getter
public class ConnectionPointIn<T> extends ConnectionPoint {
    private final VarInput<T> inputVar;

    public ConnectionPointIn(NodePanel nodePanel, VarInput<T> varInput) {
        super(nodePanel);
        this.inputVar = varInput;
    }
}
