package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class NodeConnection extends Connection {
//    private NodePanel startNodePanel;
//    private NodePanel endNodePanel;
    private final ConnectionPoint startPoint;
    private final ConnectionPoint endPoint;

    public NodeConnection(ConnectionPointOut<?> start, ConnectionPointIn<?> end) {
        super(start == null ? new Point() : start.getLocation(), end == null ? new Point() : end.getLocation());
//        this.startNodePanel = start;
//        this.endNodePanel = end;
        this.startPoint = start;
        this.endPoint = end;
//        if (this.startNodePanel != null)
//            this.startNodePanel.addConnection(this);
//        if (this.endNodePanel != null)
//            this.endNodePanel.addConnection(this);
        if (this.startPoint != null)
            this.startPoint.getNodePanel().addConnection(this);
        if (this.endPoint != null)
            this.endPoint.getNodePanel().addConnection(this);
        this.recalculateBounds();
    }

    @Override
    protected void paintComponent(Graphics g) {
//        if (this.startNodePanel == null || this.endNodePanel == null)
//            return;
        if (this.startPoint == null || this.endPoint == null)
            return;
        // These coordinates are in relation to the bounds, which is a box that surrounds both start and end nodes
//        this.start.x = this.startNodePanel.getWidth() + Math.max(this.startNodePanel.getX() - this.endNodePanel.getX(), 0);
//        this.start.y = Math.max(this.startNodePanel.getY() - this.endNodePanel.getY(), 0) + this.startNodePanel.getHeight() / 2;
//        this.end.x = Math.max(this.endNodePanel.getX() - this.startNodePanel.getX(), 0);
//        this.end.y = Math.max(this.endNodePanel.getY() - this.startNodePanel.getY(), 0) + this.endNodePanel.getHeight() / 2;
        System.out.println(this.startPoint);

        NodePanel startPanel = this.startPoint.getNodePanel();
        NodePanel endPanel = this.endPoint.getNodePanel();
        this.start.x = NodePanel.PANEL_WIDTH + Math.max(startPanel.getX() - endPanel.getX(), 0);
        this.start.y = Math.max(startPanel.getY() - endPanel.getY(), 0) + NodePanel.PANEL_HEIGHT / 2;
        this.end.x = Math.max(endPanel.getX() - startPanel.getX(), 0);
        this.end.y = Math.max(endPanel.getY() - startPanel.getY(), 0) + NodePanel.PANEL_HEIGHT / 2;

        super.paintComponent(g);

        g.setColor(Color.CYAN);
        g.drawRect(0, 0, this.getWidth()-5, this.getHeight()-5);
    }

    @Override
    public void recalculateBounds() {
//        if (this.startNodePanel == null || this.endNodePanel == null)
//            return;
        if (this.startPoint == null || this.endPoint == null)
            return;
//        this.setBounds(
//            Math.min(this.startNodePanel.getX(), this.endNodePanel.getX()) - BOUNDS_OFFSET,
//            Math.min(this.startNodePanel.getY(), this.endNodePanel.getY()) - BOUNDS_OFFSET,
//            Math.abs(this.startNodePanel.getX() - this.endNodePanel.getX()) + NodePanel.PANEL_WIDTH + BOUNDS_OFFSET * 2,
//            Math.abs(this.startNodePanel.getY() - this.endNodePanel.getY()) + NodePanel.PANEL_HEIGHT + BOUNDS_OFFSET * 2
//        );
        NodePanel startPanel = this.startPoint.getNodePanel();
        NodePanel endPanel = this.endPoint.getNodePanel();
        this.setBounds(
            Math.min(startPanel.getX(), endPanel.getX()) - BOUNDS_OFFSET,
            Math.min(startPanel.getY(), endPanel.getY()) - BOUNDS_OFFSET,
            Math.abs(startPanel.getX() - endPanel.getX()) + NodePanel.PANEL_WIDTH + BOUNDS_OFFSET * 2,
            Math.abs(startPanel.getY() - endPanel.getY()) + NodePanel.PANEL_HEIGHT + BOUNDS_OFFSET * 2
        );
    }
}
