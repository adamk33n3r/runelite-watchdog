package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectedVariable;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractInput<T> extends JPanel implements ConnectedVariable<T> {
    public AbstractInput() {
        this.setLayout(new BorderLayout(5, 5));
    }
}
