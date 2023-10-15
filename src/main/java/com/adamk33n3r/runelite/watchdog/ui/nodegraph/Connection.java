package com.adamk33n3r.runelite.watchdog.ui.nodegraph;


import javax.swing.JComponent;
import java.awt.*;
import java.awt.geom.CubicCurve2D;

public class Connection extends JComponent {
    protected static final int END_SIZE = 10;
    protected static final int BOUNDS_OFFSET = 40;

    protected Point start;
    protected Point end;

    public Connection(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.recalculateBounds();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double startX = this.start.x;
        double startY = this.start.y;
        double endX = this.end.x;
        double endY = this.end.y;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.translate(BOUNDS_OFFSET, BOUNDS_OFFSET);

        CubicCurve2D.Double curve = new CubicCurve2D.Double(
            startX, startY,
            startX + 100, startY,
            endX - 100, endY,
            endX, endY);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.draw(curve);

        g.setColor(Color.GREEN);
        g.fillRect((int) startX - END_SIZE/2, (int) (startY - END_SIZE / 2), END_SIZE, END_SIZE);
        g.setColor(Color.RED);
        g.fillRect((int) (endX - END_SIZE/2), (int) (endY - END_SIZE / 2), END_SIZE, END_SIZE);

//        g.setColor(Color.red);
//        g.drawRect(0, 0, this.getWidth() - BOUNDS_OFFSET*2, this.getHeight() - BOUNDS_OFFSET*2);

        g.translate(-BOUNDS_OFFSET, -BOUNDS_OFFSET);
    }

    public void recalculateBounds() {
        this.setBounds(
            Math.min(this.start.x, this.end.x) - BOUNDS_OFFSET,
            Math.min(this.start.y, this.end.y) - BOUNDS_OFFSET,
            Math.abs(this.start.x - this.end.x) + BOUNDS_OFFSET * 2,
            Math.abs(this.start.y - this.end.y) + BOUNDS_OFFSET * 2
        );
        System.out.println(this.getBounds());
    }
}
