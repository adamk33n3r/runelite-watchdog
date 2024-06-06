package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class ConnectionLine<T> extends JPanel {
    private final ConnectionPointIn<T> in;
    private final ConnectionPointOut<T> out;

    public ConnectionLine(@Nullable ConnectionPointIn<T> in, ConnectedVariable<T> variable, @Nullable ConnectionPointOut<T> out) {
        this.in = in;
        this.out = out;
        this.setLayout(new BorderLayout(5, 5));
        if (this.in != null) {
            this.add(this.in, BorderLayout.WEST);
            this.in.getInputVar().onChange((newValue) -> {
                variable.setValue(newValue); // Sets the value of the swing component
                if (this.out != null) {
                    this.out.getOutputVar().setValue(newValue);
                }
            });
            this.in.getInputVar().onConnectChange((connected) -> {
                if (connected) {
                    this.in.setBackground(Color.GREEN);
                } else {
                    this.in.setBackground(Color.RED);
                }
            });
        }


        if (this.out != null) {
            this.add(this.out, BorderLayout.EAST);
            variable.onChange((newValue) -> {
                this.out.getOutputVar().setValue(newValue);
            });
            this.out.getOutputVar().onConnectChange((connected) -> {
                if (connected) {
                    this.out.setBackground(Color.GREEN);
                } else {
                    this.out.setBackground(Color.RED);
                }
            });
        }
        this.add(variable.getComponent(), BorderLayout.CENTER);
    }
}
