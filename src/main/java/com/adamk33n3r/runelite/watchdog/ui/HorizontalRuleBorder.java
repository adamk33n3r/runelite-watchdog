package com.adamk33n3r.runelite.watchdog.ui;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

public class HorizontalRuleBorder extends EtchedBorder {
    private final int size;
    private final Border outsideBorder;
    public HorizontalRuleBorder(int size) {
        super();
        this.size = size;
        this.outsideBorder = new EmptyBorder(this.size, 0, 0, 0);
    }

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     *
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets outerInsets = this.outsideBorder.getBorderInsets(c);
        insets.set(this.size + outerInsets.top, 0, 0, 0);
        return insets;
    }

    /**
     * Paints the border for the specified component with the
     * specified position and size.
     *
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets  nextInsets;
        int px, py, pw, ph;

        px = x;
        py = y;
        pw = width;
        ph = height;

        outsideBorder.paintBorder(c, g, px, py, pw, ph);

        nextInsets = outsideBorder.getBorderInsets(c);
        px += nextInsets.left;
        py += nextInsets.top;
        pw = pw - nextInsets.right - nextInsets.left;
        ph = ph - nextInsets.bottom - nextInsets.top;

        g.translate(px, py);

        g.setColor(etchType == LOWERED? getShadowColor(c) : getHighlightColor(c));
        g.drawLine(0, 0, pw - 2, 0);

        g.setColor(etchType == LOWERED? getHighlightColor(c) : getShadowColor(c));
        g.drawLine(1, 1, pw-3, 1);

        g.translate(-px, -py);
    }
}
