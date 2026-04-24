package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.TextInput;

import javax.swing.*;
import java.awt.*;

public class NoteNodePanel extends NodePanel {
    public NoteNodePanel(GraphPanel graphPanel, NoteNode node, int x, int y, Color color) {
        super(graphPanel, node, x, y, node.getOriginalType() != null ? "Unknown: " + node.getOriginalType() : "Note", color);

        if (node.getOriginalType() != null) {
            JLabel label = new JLabel("<html><i>Unknown node type: " + node.getOriginalType() + "</i></html>");
            label.setForeground(Color.ORANGE);
            this.items.add(label);
        } else {
            ConnectionPointIn<String> noteIn = new ConnectionPointIn<>(this, node.getNote());
            TextInput textInput = new TextInput("Enter note...", "Note text", node.getNote());
            this.items.add(new ConnectionLine<>(noteIn, textInput, null));
            this.watchDirty(node.getNote());
        }

        this.pack();
    }
}
