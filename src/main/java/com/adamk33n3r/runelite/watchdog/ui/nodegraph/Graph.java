package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;

import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Consumer;

public class Graph extends JPanel {
    private static final int ZOOM_FACTOR = 10; // Adjust this value for zoom sensitivity
    private static final double MIN_ZOOM = 0.1; // Minimum zoom level
    private static final double MAX_ZOOM = 3.0; // Maximum zoom level
    private static final BufferedImage BACKGROUND_IMG = ImageUtil.loadImageResource(Graph.class, "graph-bg.png");
    private final JPanel nodes = new JPanel();
    private final JPanel connections = new JPanel();
    private final double zoomLevel = 1;
    private final Map<Component, Dimension> originalSizes = new HashMap<>();
    private Point popupLocation;

    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    public void init() {
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

        ChatAlert alert = new ChatAlert("Test Chat Alert");
        alert.setMessage("This is a test message");
        Node gameMessageNode = new AlertNode(this, 50, 50, alert.getType().getName(), Color.red, alert);
        this.nodes.add(gameMessageNode);
        ScreenFlash screenFlash = new ScreenFlash();
        Node screenFlashNode = new NotificationNode(this, 750, 350, "Screen Flash", Color.green, screenFlash, colorPickerManager);
        this.nodes.add(screenFlashNode);
        Node logicNode = new IfNode(this, 400, 200, "If Node", Color.CYAN);
        this.nodes.add(logicNode);
        TextToSpeech tts = new TextToSpeech();
//        tts.setSource(TTSSource.ELEVEN_LABS);
        Node textToSpeechNode = new NotificationNode(this, 700, 500, "Text to Speech", Color.green, tts, colorPickerManager);
        this.nodes.add(textToSpeechNode);

        SpawnedAlert spawnedAlert = new SpawnedAlert("Spawned Alert");
        spawnedAlert.setPattern("Henry");
        spawnedAlert.setSpawnedDespawned(SpawnedAlert.SpawnedDespawned.SPAWNED);
        spawnedAlert.setSpawnedType(SpawnedAlert.SpawnedType.NPC);
        Node node4 = new AlertNode(this, 150, 500, spawnedAlert.getType().getName(), Color.red, spawnedAlert);
        this.nodes.add(node4);

        this.connect(gameMessageNode, logicNode);
        this.connect(logicNode, screenFlashNode);
        this.connect(gameMessageNode, textToSpeechNode);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Graph.this.grabFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Graph.this.createNode(e.getComponent(), e.getX(), e.getY(), null, (s) -> {});
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

    public void createNode(Component parent, int x, int y, Class<? extends Enum<?>>[] filter, Consumer<Node> onSelect) {
        this.popupLocation = SwingUtilities.convertPoint(parent, new Point(x, y), this);

        new NewNodePopup(filter).show(parent, x, y, (selected) -> {
            System.out.println(selected);
            if (selected instanceof TriggerType) {
                System.out.println("create alert node");
                Node node = new AlertNode(Graph.this, Graph.this.popupLocation.x, Graph.this.popupLocation.y, ((TriggerType) selected).getName(), Color.CYAN, null);
                Graph.this.nodes.add(node, 0);
                onSelect.accept(node);
            } else if (selected instanceof NotificationType) {
                System.out.println("create notification node");
                Node node = new NotificationNode(Graph.this, Graph.this.popupLocation.x, Graph.this.popupLocation.y, ((NotificationType) selected).getName(), Color.ORANGE, null, colorPickerManager);
                Graph.this.nodes.add(node, 0);
                onSelect.accept(node);
            } else if (selected instanceof LogicNodeType) {
                LogicNodeType logicNodeType = (LogicNodeType) selected;
                switch (logicNodeType) {
                    case AND:
                    case OR:
                    case GREATER_THAN:
                    case LESS_THAN:
                    case EQUALS:
                    case NOT_EQUALS:
                        Node node = new IfNode(Graph.this, Graph.this.popupLocation.x, Graph.this.popupLocation.y, logicNodeType.getName(), Color.MAGENTA);
                        Graph.this.nodes.add(node, 0);
                        onSelect.accept(node);
                        break;
                }
            }
            Graph.this.revalidate();
            Graph.this.repaint();
        });
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

    public void removeNode(Node node) {
        this.nodes.remove(node);
        Arrays.stream(this.connections.getComponents())
            .filter(component -> component instanceof NodeConnection)
            .map(component -> (NodeConnection) component)
            .filter(conn -> conn.getStartNode() == node || conn.getEndNode() == node)
            .forEach(this.connections::remove);
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
