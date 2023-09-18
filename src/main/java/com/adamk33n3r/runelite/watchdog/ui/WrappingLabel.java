package com.adamk33n3r.runelite.watchdog.ui;

import javax.swing.*;

public class WrappingLabel extends JTextArea {
    public WrappingLabel(String text) {
        super(text);

        this.setOpaque(false);
        this.setEditable(false);
        this.setFocusable(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
    }
}
