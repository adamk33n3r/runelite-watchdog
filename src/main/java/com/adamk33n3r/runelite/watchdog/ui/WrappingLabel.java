package com.adamk33n3r.runelite.watchdog.ui;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class WrappingLabel extends JTextArea {
    public WrappingLabel(String text) {
        super();

        this.setOpaque(false);
        this.setEditable(false);
        this.setFocusable(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);

        // Need to update this before setting the text, or it will cause the scroll pane to scroll to this component
        ((DefaultCaret)this.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        this.setText(text);
    }
}
