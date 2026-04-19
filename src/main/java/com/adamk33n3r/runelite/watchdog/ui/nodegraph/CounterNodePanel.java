package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.flow.Counter;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class CounterNodePanel extends NodePanel {
    private final ConnectionPointIn<ExecSignal> execIn;
    private final ConnectionPointIn<ExecSignal> resetIn;
    private final ConnectionPointOut<ExecSignal> execOut;
    private final ConnectionPointOut<Number> countOut;

    public CounterNodePanel(GraphPanel graphPanel, int x, int y, Counter node, Color color) {
        super(graphPanel, node, x, y, "Counter", color);

        this.execIn = new ConnectionPointIn<>(this, node.getExec());
        this.execOut = new ConnectionPointOut<>(this, node.getExecOut());
        this.items.add(new ConnectionLine<>(this.execIn, new ViewInput<>("Exec", node.getExec().getValue()), this.execOut));

        this.resetIn = new ConnectionPointIn<>(this, node.getReset());
        this.items.add(new ConnectionLine<>(this.resetIn, new ViewInput<>("Reset", null), null));

        this.countOut = new ConnectionPointOut<>(this, node.getCountOut());
        ViewInput<Number> resultView = new ViewInput<>("Count", node.getCountOut().getValue());
        addDisposer(node.getExec().onChange(a -> resultView.setValue(node.getCountOut().getValue())));
        addDisposer(node.getCount().onChange(a -> resultView.setValue(node.getCountOut().getValue())));
        addDisposer(node.getReset().onChange(a -> resultView.setValue(node.getCountOut().getValue())));
        this.items.add(new ConnectionLine<>(null, resultView, this.countOut));

        this.pack();
    }
}
