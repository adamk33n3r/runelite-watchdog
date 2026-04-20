package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A connection-line row where the input pin and output pin have different types.
 * Unlike {@link ConnectionLine}, the middle widget does NOT drive the output — the output
 * is expected to be fired externally (e.g. by Graph.executeExecChainBFS for exec signals).
 */
public class SplitConnectionLine<IN, OUT> extends JPanel {
    private final List<Runnable> disposers = new ArrayList<>();

    public SplitConnectionLine(
        @Nullable ConnectionPointIn<IN> in,
        ConnectedVariable<IN> variable,
        @Nullable ConnectionPointOut<OUT> out
    ) {
        this.setLayout(new BorderLayout(5, 5));
        this.setOpaque(false);

        if (in != null) {
            variable.setValue(in.getInputVar().getValue());
            this.add(in, BorderLayout.WEST);
            disposers.add(in.getInputVar().onChange(variable::setValue));
            disposers.add(in.getInputVar().onConnectChange((_c) -> {
                boolean connected = in.getInputVar().isConnected();
                in.setConnected(connected);
                variable.setEnabled(!connected);
            }));
        }

        // Wire only the visual connected-state for the output — NOT the widget→output value propagation.
        if (out != null) {
            this.add(out, BorderLayout.EAST);
            disposers.add(out.getOutputVar().onConnectChange((_c) ->
                out.setConnected(out.getOutputVar().isConnected())));
        }

        this.add(variable.getComponent(), BorderLayout.CENTER);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.disposers.forEach(Runnable::run);
        this.disposers.clear();
    }
}
