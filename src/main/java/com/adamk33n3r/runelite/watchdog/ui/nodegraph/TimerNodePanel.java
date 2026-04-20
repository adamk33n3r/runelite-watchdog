package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.flow.TimerNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.SplitConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.NumberInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;

import lombok.Getter;

import java.awt.Color;

@Getter
public class TimerNodePanel extends NodePanel {
    private final ConnectionPointIn<ExecSignal> execIn;
    private final ConnectionPointIn<ExecSignal> resetIn;
    private final ConnectionPointIn<Number> durationMsIn;
    private final ConnectionPointIn<Boolean> pulseIn;
    private final ConnectionPointOut<ExecSignal> execOut;
    private final ConnectionPointOut<ExecSignal> pulseOut;

    public TimerNodePanel(GraphPanel graphPanel, int x, int y, TimerNode node, Color color) {
        super(graphPanel, node, x, y, "Timer", color);

        this.execIn = new ConnectionPointIn<>(this, node.getExec());
        this.execOut = new ConnectionPointOut<>(this, node.getExecOut());
        this.items.add(new ConnectionLine<>(this.execIn, new ViewInput<>("Exec", node.getExec().getValue()), this.execOut));

        this.resetIn = new ConnectionPointIn<>(this, node.getReset());
        this.items.add(new ConnectionLine<>(this.resetIn, new ViewInput<>("Reset", null), null));

        this.durationMsIn = new ConnectionPointIn<>(this, node.getDurationMs());
        this.items.add(new ConnectionLine<>(this.durationMsIn, new NumberInput("Duration (ms)", node.getDurationMs()), null));

        this.pulseIn = new ConnectionPointIn<>(this, node.getPulse());
        this.pulseOut = new ConnectionPointOut<>(this, node.getPulseOut());
        this.items.add(new SplitConnectionLine<>(this.pulseIn, new BoolInput("Pulse", node.getPulse()), this.pulseOut));

        this.pack();
    }
}
