package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.flow.DelayNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.IntegerInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;

import lombok.Getter;

import java.awt.Color;

@Getter
public class DelayNodePanel extends NodePanel {
    private final ConnectionPointIn<ExecSignal> execIn;
    private final ConnectionPointOut<ExecSignal> execOut;
    private final ConnectionPointIn<Number> delayMsIn;

    public DelayNodePanel(GraphPanel graphPanel, int x, int y, DelayNode delayNode, Color color) {
        super(graphPanel, delayNode, x, y, "Delay", color);

        this.execIn = new ConnectionPointIn<>(this, delayNode.getExec());
        this.execOut = new ConnectionPointOut<>(this, delayNode.getExecOut());
        this.items.add(new ConnectionLine<>(this.execIn, new ViewInput<>("Exec", delayNode.getExec().getValue()), this.execOut));

        this.delayMsIn = new ConnectionPointIn<>(this, delayNode.getDelayMs());
        this.items.add(new ConnectionLine<>(this.delayMsIn, new IntegerInput("Delay (ms)", delayNode.getDelayMs()), null));

        this.pack();
    }
}
