package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class NodeConnection extends Connection {
    private Node startNode;
    private Node endNode;

    public NodeConnection(Node start, Node end) {
        super(start == null ? new Point() : start.getLocation(), end == null ? new Point() : end.getLocation());
        this.startNode = start;
        this.endNode = end;
        if (this.startNode != null)
            this.startNode.addConnection(this);
        if (this.endNode != null)
            this.endNode.addConnection(this);
        this.recalculateBounds();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (this.startNode == null || this.endNode == null)
            return;
        // These coordinates are in relation to the bounds, which is a box that surrounds both start and end nodes
        this.start.x = this.startNode.getWidth() + Math.max(this.startNode.getX() - this.endNode.getX(), 0);
        this.start.y = Math.max(this.startNode.getY() - this.endNode.getY(), 0) + this.startNode.getHeight() / 2;
        this.end.x = Math.max(this.endNode.getX() - this.startNode.getX(), 0);
        this.end.y = Math.max(this.endNode.getY() - this.startNode.getY(), 0) + this.endNode.getHeight() / 2;

        super.paintComponent(g);

//        g.setColor(Color.CYAN);
//        g.drawRect(0, 0, this.getWidth()-5, this.getHeight()-5);
    }

    @Override
    public void recalculateBounds() {
        if (this.startNode == null || this.endNode == null)
            return;
        this.setBounds(
            Math.min(this.startNode.getX(), this.endNode.getX()) - BOUNDS_OFFSET,
            Math.min(this.startNode.getY(), this.endNode.getY()) - BOUNDS_OFFSET,
            Math.abs(this.startNode.getX() - this.endNode.getX()) + Node.PANEL_WIDTH + BOUNDS_OFFSET * 2,
            Math.abs(this.startNode.getY() - this.endNode.getY()) + Node.PANEL_HEIGHT + BOUNDS_OFFSET * 2
        );
    }
}
