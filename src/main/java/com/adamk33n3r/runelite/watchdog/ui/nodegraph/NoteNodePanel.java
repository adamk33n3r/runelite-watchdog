package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;

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
            JTextArea textArea = new JTextArea(node.getNote(), 3, 16);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { node.setNote(textArea.getText()); graphPanel.notifyChange(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { node.setNote(textArea.getText()); graphPanel.notifyChange(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            });
            this.items.add(new JScrollPane(textArea));
        }

        this.pack();
    }
}
