package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConnectionPoint extends JComponent {
    private DragConnection newConnection;

    public ConnectionPoint(Node node) {
        System.out.println("new connection point");
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println("mouse pressed");
                Point point = SwingUtilities.convertPoint(ConnectionPoint.this, e.getPoint(), node);
                point.x -= Node.PANEL_WIDTH;
                ConnectionPoint.this.newConnection = new DragConnection(node, point);
                node.getGraph().add(ConnectionPoint.this.newConnection, 0);
                node.getGraph().revalidate();
                node.getGraph().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("mouse released");
                System.out.println(ConnectionPoint.this.newConnection);
                node.getGraph().remove(ConnectionPoint.this.newConnection);
                node.getGraph().revalidate();
                node.getGraph().repaint();
                ConnectionPoint.this.newConnection = null;

                Point point = SwingUtilities.convertPoint(ConnectionPoint.this, e.getPoint(), node.getGraph());
                Component deepestComponentAt = SwingUtilities.getDeepestComponentAt(node.getGraph(), point.x, point.y);
                NotificationNode droppedNode = (NotificationNode) SwingUtilities.getAncestorOfClass(NotificationNode.class, deepestComponentAt);
                if (droppedNode == null || droppedNode.equals(node)) {
                    return;
                }
                System.out.println(point);
                System.out.println(droppedNode);
                System.out.println(droppedNode.getName());
                node.getGraph().connect(node, droppedNode);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (ConnectionPoint.this.newConnection == null)
                    return;
                Point point = e.getPoint();
                point = SwingUtilities.convertPoint(ConnectionPoint.this, point, node);
//                point = SwingUtilities.convertPoint(ConnectionPoint.this, point, node);
                point.x -= Node.PANEL_WIDTH;
//                SwingUtilities.convertPointToScreen(point, ConnectionPoint.this);
                ConnectionPoint.this.newConnection.setEndOffset(point);
                ConnectionPoint.this.newConnection.repaint();
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = this.getSize();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width, size.height);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }
}
