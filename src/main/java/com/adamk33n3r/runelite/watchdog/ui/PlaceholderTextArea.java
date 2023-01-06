package com.adamk33n3r.runelite.watchdog.ui;

import lombok.Getter;
import lombok.Setter;

import javax.swing.JTextArea;
import javax.swing.text.Document;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Map;

public class PlaceholderTextArea extends JTextArea {
    @Getter
    @Setter
    private String placeholder;

    private static final Map<?, ?> hints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");

    public PlaceholderTextArea() {
        super();
    }

    public PlaceholderTextArea(final Document pDoc, final String pText, final int pRows, final int pColumns) {
        super(pDoc, pText, pRows, pColumns);
    }

    public PlaceholderTextArea(final String pText) {
        super(pText);
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (this.placeholder == null || this.placeholder.length() == 0 || this.getText().length() > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(this.getDisabledTextColor());
        g.drawString(this.placeholder, this.getInsets().left, g.getFontMetrics().getMaxAscent() + this.getInsets().top);
    }
}
