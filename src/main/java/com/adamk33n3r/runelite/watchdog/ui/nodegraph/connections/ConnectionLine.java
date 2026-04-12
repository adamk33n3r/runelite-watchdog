package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionLine<T> extends JPanel {
    private final ConnectionPointIn<T> in;
    private final ConnectionPointOut<T> out;
    private final List<Runnable> disposers = new ArrayList<>();

    public ConnectionLine(@Nullable ConnectionPointIn<T> in, ConnectedVariable<T> variable, @Nullable ConnectionPointOut<T> out) {
        this.in = in;
        this.out = out;
        this.setLayout(new BorderLayout(5, 5));
        this.setOpaque(false);
        if (this.in != null) {
            variable.setValue(this.in.getInputVar().getValue());
            this.add(this.in, BorderLayout.WEST);
            disposers.add(this.in.getInputVar().onChange((newValue) -> {
                variable.setValue(newValue); // Sets the value of the swing component
                if (this.out != null) {
                    this.out.getOutputVar().setValue(newValue);
                }
            }));
            disposers.add(this.in.getInputVar().onConnectChange((connected) -> {
                this.in.setConnected(connected);
                variable.setEnabled(!connected);
            }));
        }

        if (this.out != null) {
            this.add(this.out, BorderLayout.EAST);
            variable.registerOnChange((newValue) -> {
                this.out.getOutputVar().setValue(newValue);
            });
            disposers.add(this.out.getOutputVar().onConnectChange((connected) -> {
                this.out.setConnected(connected);
            }));
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
