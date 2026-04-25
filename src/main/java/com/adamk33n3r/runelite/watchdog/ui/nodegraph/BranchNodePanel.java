package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.nodes.flow.Branch;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.BoolInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;

import java.awt.Color;

@Getter
public class BranchNodePanel extends NodePanel {
    private final ConnectionPointIn<ExecSignal> execIn;
    private final ConnectionPointIn<Boolean> conditionIn;
    private final ConnectionPointOut<ExecSignal> execTrue;
    private final ConnectionPointOut<ExecSignal> execFalse;

    public BranchNodePanel(GraphPanel graphPanel, int x, int y, Branch node, Color color) {
        super(graphPanel, node, x, y, "Branch", color);

        // Exec in → "True" exec out
        this.execIn = new ConnectionPointIn<>(this, node.getExec());
        this.execTrue = new ConnectionPointOut<>(this, node.getExecTrue());
        this.items.add(new ConnectionLine<>(this.execIn, new ViewInput<>("Exec", node.getExec().getValue()), null));
        this.items.add(new ConnectionLine<>(null, new ViewInput<>("True", null), this.execTrue));

        // Condition in (Boolean), no output on this row
        this.conditionIn = new ConnectionPointIn<>(this, node.getCondition());
        this.items.add(new ConnectionLine<>(this.conditionIn, new BoolInput("Condition", node.getCondition()), null));

        // False exec out only (no input on this row)
        this.execFalse = new ConnectionPointOut<>(this, node.getExecFalse());
        this.items.add(new ConnectionLine<>(null, new ViewInput<>("False", null), this.execFalse));

        this.watchDirty(node.getCondition());
        this.pack();
    }
}
