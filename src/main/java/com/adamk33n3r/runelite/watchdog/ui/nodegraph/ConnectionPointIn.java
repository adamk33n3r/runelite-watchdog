package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import lombok.Getter;

import java.lang.reflect.Type;

@Getter
public class ConnectionPointIn<T> extends ConnectionPoint {
    private final VarInput<T> inputVar;

    public ConnectionPointIn(NodePanel nodePanel, String name, Class<T> type, T initialValue) {
        super(nodePanel);
        this.inputVar = new VarInput<>(nodePanel.getNode(), name, type, initialValue);
    }
}
