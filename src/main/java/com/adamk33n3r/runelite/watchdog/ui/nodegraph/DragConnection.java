package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import java.awt.*;

public class DragConnection extends Connection {
    private final NodePanel startNodePanel;
    private final ConnectionPointOut<?> startPoint;
    public DragConnection(NodePanel start, ConnectionPointOut<?> startPoint, Point end) {
        super(start.getLocation(), end);
        this.startNodePanel = start;
        this.startPoint = startPoint;
        this.recalculateBounds();
    }

    public void setEndOffset(Point endOffset) {
        this.end.x = endOffset.x;
        this.end.y = endOffset.y;
        this.recalculateBounds();
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.start.x = this.end.x > 0 ? 0 : -this.end.x;
        this.start.y = this.startNodePanel.getY() - this.getY() - BOUNDS_OFFSET + this.startNodePanel.getOutConnectionPoints().getY() + this.startPoint.getY() + this.startPoint.getSize().height / 2;

        // I think this doesn't break things because we are setting the bounds every time
        this.end.x = Math.max(0, this.end.x);
        this.end.y = Math.max(0, this.end.y);

        super.paintComponent(g);

        g.setColor(Color.CYAN);
        g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
    }

    @Override
    public void recalculateBounds() {
        if (this.startNodePanel == null)
            return;
        int baseX = this.startNodePanel.getX() + NodePanel.PANEL_WIDTH;

        this.setBounds(
            Math.min(baseX, baseX + this.end.x) - BOUNDS_OFFSET,
            Math.min(this.startNodePanel.getY(), this.startNodePanel.getY() + this.end.y) - BOUNDS_OFFSET,
            Math.abs(baseX - (baseX + this.end.x)) + BOUNDS_OFFSET * 2,
            NodePanel.PANEL_HEIGHT + Math.max(0, this.end.y - NodePanel.PANEL_HEIGHT) - Math.min(0, this.end.y) + BOUNDS_OFFSET * 2
        );
//        System.out.println(this.getBounds());
//        int i = Math.abs(baseX - (baseX + this.end.x));
//        int i2 = i + BOUNDS_OFFSET * 2;
//        System.out.printf("%s:%s%n", i, i2);
//        this.setBounds(0, 0, 5000, 5000);
    }
}
