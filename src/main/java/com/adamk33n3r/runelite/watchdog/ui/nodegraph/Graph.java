package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;

public class Graph extends JPanel {
    private static final int ZOOM_FACTOR = 10; // Adjust this value for zoom sensitivity
    private static final double MIN_ZOOM = 0.1; // Minimum zoom level
    private static final double MAX_ZOOM = 3.0; // Maximum zoom level
    private static final BufferedImage BACKGROUND_IMG = ImageUtil.loadImageResource(Graph.class, "graph-bg.png");
    private final JPanel nodes = new JPanel();
    private final JPanel connections = new JPanel();
    private final double zoomLevel = 1;
    private final Map<Component, Dimension> originalSizes = new HashMap<>();
    private final JPopupMenu createNodePopup;
    private Point popupLocation;

    public Graph() {
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setLayout(null);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(6000, 4000));
        this.setName("Graph");
        this.nodes.setName("Nodes");
        this.connections.setName("Connections");
        SwingUtilities.invokeLater(() -> {
            this.nodes.setBounds(0, 0, this.getWidth(), this.getHeight());
            this.connections.setBounds(0, 0, this.getWidth(), this.getHeight());
        });

        this.nodes.setOpaque(false);
        this.nodes.setLayout(null);
        this.add(this.nodes);
        this.connections.setOpaque(false);
        this.connections.setLayout(null);
        this.add(this.connections);

        Node node1 = new AlertNode(this, 150, 150, "Game Message", Color.red);
        this.nodes.add(node1);
        Node node2 = new NotificationNode(this, 700, 150, "Screen Flash", Color.green);
        this.nodes.add(node2);
        Node node3 = new NotificationNode(this, 700, 500, "Text to Speech", Color.green);
        this.nodes.add(node3);
        Node node4 = new AlertNode(this, 150, 500, "Notification Fired", Color.red);
        this.nodes.add(node4);

//        this.connect(node1, node2);
//        this.connect(node1, node3);
//        this.connect(node4, node3);

        this.createNodePopup = new NewNodePopup((selected) -> {
            System.out.println(selected);
            if (selected instanceof TriggerType) {
                System.out.println("create alert node");
                Node node = new AlertNode(this, this.popupLocation.x, this.popupLocation.y, ((TriggerType) selected).getName(), Color.CYAN);
                this.nodes.add(node, 0);
            } else if (selected instanceof NotificationType) {
                System.out.println("create notification node");
                Node node = new NotificationNode(this, this.popupLocation.x, this.popupLocation.y, ((NotificationType) selected).getName(), Color.ORANGE);
                this.nodes.add(node, 0);
            }
            Graph.this.revalidate();
            Graph.this.repaint();
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Graph.this.grabFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Graph.this.popupLocation = e.getPoint();
                    Graph.this.createNodePopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        DraggingMouseAdapter draggingGraph = new DraggingMouseAdapter((start, point) -> {
            int deltaX = start.x - point.x;
            int deltaY = start.y - point.y;
            JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, Graph.this);
            Rectangle viewRect = viewport.getViewRect();
            viewRect.x += deltaX;
            viewRect.y += deltaY;
            this.scrollRectToVisible(viewRect);
        });
        this.addMouseListener(draggingGraph);
        this.addMouseMotionListener(draggingGraph);
//        this.addMouseWheelListener(new MouseAdapter() {
//            @Override
//            public void mouseWheelMoved(MouseWheelEvent e) {
//                if (e.getWheelRotation() < 0) {
//                    // Zoom in
//                    zoomLevel = Math.min(MAX_ZOOM, zoomLevel + (1.0 / ZOOM_FACTOR));
//                } else {
//                    // Zoom out
//                    zoomLevel = Math.max(MIN_ZOOM, zoomLevel - (1.0 / ZOOM_FACTOR));
//                }
//                rescaleComponents(Graph.this, zoomLevel);
//                repaint();
//            }
//        });
    }

    public void connect(Node node1, Node node2) {
        if (node1.getConnections().stream().anyMatch(c -> c.getEndNode().equals(node2))) {
            System.out.println("already connected");
            return;
        }
        Connection conn = new NodeConnection(node1, node2);
//        this.setComponentZOrder(conn, 0);
        this.connections.add(conn);
    }

    public void onNodeMoved(Node node) {
        for (Connection connection : node.getConnections()) {
            connection.recalculateBounds();
        }
    }

    public void moveNodeToTop(Node node) {
        this.nodes.remove(node);
        this.nodes.add(node, 0);
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D)g).scale(this.zoomLevel, this.zoomLevel);
        int width = this.getWidth();
        int height = this.getHeight();
//        this.nodes.setBounds(0, 0, this.getWidth(), this.getHeight());
//        this.connections.setBounds(0, 0, this.getWidth(), this.getHeight());
        for (int x = 0; x < width; x += BACKGROUND_IMG.getWidth()) {
            for (int y = 0; y < height; y += BACKGROUND_IMG.getHeight()) {
                g.drawImage(BACKGROUND_IMG, x, y, this);
            }
        }
    }

    // Helper method to rescale Swing components
    private void rescaleComponents(Container container, double scale) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                this.rescaleComponents((Container) component, scale);
            }
//            Dimension originalSize = component.getPreferredSize();
//            if (!this.originalSizes.containsKey(component)) {
//                this.originalSizes.put(component, originalSize);
//            }
            component.setSize((int) (component.getWidth() * scale), (int) (component.getHeight() * scale));
        }
    }

}
