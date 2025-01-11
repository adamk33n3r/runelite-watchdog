package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;

import javax.swing.*;
import java.awt.*;

public class DragConnection extends Connection {
    private final NodePanel startNodePanel;
    private final ConnectionPointOut<?> startPoint;
    private Point endOffset;

    public DragConnection(NodePanel start, ConnectionPointOut<?> startPoint, Point end) {
        super(start.getLocation(), end);
        this.startNodePanel = start;
        this.startPoint = startPoint;
        this.endOffset = end;
        this.recalculateBounds();
    }

    public void setEndOffset(Point endOffset) {
        this.endOffset = endOffset;
        this.recalculateBounds();
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.start.x = this.endOffset.x > 0 ? 0 : -this.endOffset.x;
        Point convertedPoint = SwingUtilities.convertPoint(this.startPoint.getParent(), this.startPoint.getLocation(), this.startPoint.getNodePanel());
        this.start.y = this.startNodePanel.getY() - BOUNDS_OFFSET - this.getY() + convertedPoint.y + this.startPoint.getSize().height / 2;

        this.end.x = Math.max(0, this.endOffset.x);
        this.end.y = Math.max(0, this.endOffset.y);

        super.paintComponent(g);

//        g.setColor(Color.CYAN);
//        g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
    }

    @Override
    public void recalculateBounds() {
        if (this.startNodePanel == null)
            return;
        int baseX = this.startNodePanel.getX() + NodePanel.PANEL_WIDTH;

        this.setBounds(
            Math.min(baseX, baseX + this.endOffset.x) - BOUNDS_OFFSET,
            Math.min(this.startNodePanel.getY(), this.startNodePanel.getY() + this.endOffset.y) - BOUNDS_OFFSET,
            Math.abs(baseX - (baseX + this.endOffset.x)) + BOUNDS_OFFSET * 2,
            NodePanel.PANEL_HEIGHT + Math.max(0, this.endOffset.y - NodePanel.PANEL_HEIGHT) - Math.min(0, this.endOffset.y) + BOUNDS_OFFSET * 2
        );
    }
}
