package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;

public class Connection extends JComponent {
    private final Node start;
    private final Node end;

    private static final int END_SIZE = 10;
    private static final int BOUNDS_OFFSET = 20;

    public Connection(Node start, Node end) {
        this.start = start;
        this.end = end;
        this.start.addConnection(this);
        this.end.addConnection(this);
        this.recalculateBounds();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int startX = start.getWidth() + Math.max(start.getX() - end.getX(), 0);
        int startY = Math.max(start.getY() - end.getY(), 0) + start.getHeight() / 2;
        int endX = Math.max(end.getX() - start.getX(), 0);
        int endY = Math.max(end.getY() - start.getY(), 0) + end.getHeight() / 2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(BOUNDS_OFFSET, BOUNDS_OFFSET);
        g2.setStroke(new BasicStroke(4));

        CubicCurve2D.Float curve = new CubicCurve2D.Float(
            startX, startY,
            startX + 100, startY,
            endX - 100, endY,
            endX, endY);
        g2.setColor(Color.WHITE);
        g2.draw(curve);

        g.setColor(end.getColor());
        g.fillRect(startX, startY - END_SIZE / 2, END_SIZE, END_SIZE);
        g.setColor(start.getColor());
        g.fillRect(endX - END_SIZE, endY - END_SIZE / 2, END_SIZE, END_SIZE);

//        g.setColor(Color.cyan);
//        g.drawRect(3, 3, this.getWidth()-6, this.getHeight()-6);
    }

    public void recalculateBounds() {
        this.setBounds(
            Math.min(start.getX(), end.getX()) - BOUNDS_OFFSET,
            Math.min(start.getY(), end.getY()) - BOUNDS_OFFSET,
            Math.abs(start.getX() - end.getX())+ Node.PANEL_WIDTH + BOUNDS_OFFSET*2,
            Math.abs(start.getY() - end.getY()) + Node.PANEL_HEIGHT + BOUNDS_OFFSET*2
        );
    }
}
