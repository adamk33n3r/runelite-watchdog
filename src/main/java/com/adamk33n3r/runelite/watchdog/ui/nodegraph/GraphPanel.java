package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;

import com.adamk33n3r.runelite.watchdog.nodegraph.Graph;
import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.logic.And;
import com.adamk33n3r.runelite.watchdog.nodegraph.nodes.math.Add;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Consumer;

public class GraphPanel extends JLayeredPane {
    private static final int ZOOM_FACTOR = 10; // Adjust this value for zoom sensitivity
    private static final double MIN_ZOOM = 0.1; // Minimum zoom level
    private static final double MAX_ZOOM = 3.0; // Maximum zoom level
    private static final Integer NODE_LAYER = 1;
    private static final Integer CONNECTION_LAYER = 0;
    private static final BufferedImage BACKGROUND_IMG = ImageUtil.loadImageResource(GraphPanel.class, "graph-bg.png");
    private double zoomLevel = 1;
    private final Map<Component, Dimension> originalSizes = new HashMap<>();
    private Point popupLocation;
    private Graph graph;

    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    public void init() {
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(6000, 4000));
        this.setName("Graph");

        ChatAlert alert = new ChatAlert("Test Chat Alert");
        alert.setMessage("This is a test message");
//        NodePanel gameMessageNodePanel = new AlertNodePanel(this, 50, 50, alert.getType().getName(), Color.red, alert);
//        this.add(gameMessageNodePanel, NODE_LAYER);
        ScreenFlash screenFlash = new ScreenFlash();
//        NodePanel screenFlashNodePanel = new NotificationNodePanel(this, 750, 350, "Screen Flash", Color.green, screenFlash, colorPickerManager);
//        this.add(screenFlashNodePanel, NODE_LAYER);
        NodePanel logicNodePanel = new IfNodePanel(this, null, 400, 200, "If Node", Color.CYAN);
        this.add(logicNodePanel, NODE_LAYER);
        TextToSpeech tts = new TextToSpeech();
//        tts.setSource(TTSSource.ELEVEN_LABS);
//        NodePanel textToSpeechNodePanel = new NotificationNodePanel(this, 700, 500, "Text to Speech", Color.green, tts, colorPickerManager);
//        this.add(textToSpeechNodePanel, NODE_LAYER);

        SpawnedAlert spawnedAlert = new SpawnedAlert("Spawned Alert");
        spawnedAlert.setPattern("Henry");
        spawnedAlert.setSpawnedDespawned(SpawnedAlert.SpawnedDespawned.SPAWNED);
        spawnedAlert.setSpawnedType(SpawnedAlert.SpawnedType.NPC);
//        NodePanel nodePanel4 = new AlertNodePanel(this, 150, 500, spawnedAlert.getType().getName(), Color.red, spawnedAlert);
//        this.add(nodePanel4, NODE_LAYER);

//        this.connect(gameMessageNodePanel, logicNodePanel);
//        this.connect(logicNodePanel, screenFlashNodePanel);
//        this.connect(gameMessageNodePanel, textToSpeechNodePanel);




        this.graph = new Graph();
        TriggerNode triggerNode = new TriggerNode();
        triggerNode.setAlert(alert);
        graph.add(triggerNode);
        NotificationNode notificationNode = new NotificationNode();
        notificationNode.setNotification(tts);
        graph.add(notificationNode);

        graph.connect(triggerNode.getCaptureGroups(), notificationNode.getCaptureGroups());

        AlertNodePanel test = new AlertNodePanel(this, 50, 50, "Test", Color.RED, triggerNode);
        this.add(test, NODE_LAYER);
        NotificationNodePanel testTEST = new NotificationNodePanel(this, 700, 500, "TestTEST", Color.PINK, notificationNode, colorPickerManager);
        this.add(testTEST, NODE_LAYER);
        this.connect(test.getOutputConnectionPoint(), testTEST.getInputConnectionPoint());


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GraphPanel.this.grabFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    GraphPanel.this.createNode(e.getComponent(), e.getX(), e.getY(), null, (s) -> {});
                }
            }
        });
        DraggingMouseAdapter draggingGraph = new DraggingMouseAdapter((start, point) -> {
            int deltaX = start.x - point.x;
            int deltaY = start.y - point.y;
            JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, GraphPanel.this);
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

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    System.out.println("Dump Panels");
                    System.out.println("Nodes:");
                    for (Component component : GraphPanel.this.getComponentsInLayer(NODE_LAYER)) {
                        if (component instanceof NodePanel) {
                            NodePanel nodePanel = (NodePanel) component;
                            System.out.println(nodePanel.getName());
                        }
                    }
                    System.out.println("Connections:");
                    for (Component component : GraphPanel.this.getComponentsInLayer(CONNECTION_LAYER)) {
                        if (component instanceof NodeConnection) {
                            NodeConnection nodeConnection = (NodeConnection) component;
                            System.out.println(nodeConnection.getStartPoint().getNodePanel().getName() + " -> " + nodeConnection.getEndPoint().getNodePanel().getName());
                        }
                    }

                    System.out.println("DUMP GRAPH");
                    System.out.println(GraphPanel.this.graph);
                }
            }
        });
    }

//    public void connect(NodePanel nodePanel1, NodePanel nodePanel2) {
//        Optional<NodeConnection> first = nodePanel1.getConnections().stream().filter(c -> c.getEndNodePanel().equals(nodePanel2)).findFirst();
//        if (first.isPresent()) {
//            System.out.println("already connected");
//            nodePanel1.getConnections().remove(first.get());
//            nodePanel2.getConnections().remove(first.get());
//            this.remove(first.get());
//            return;
//        }
//        Connection conn = new NodeConnection(nodePanel1, nodePanel2);
//        this.add(conn, CONNECTION_LAYER);
//    }

    public <T> void connect(ConnectionPointOut<T> output, ConnectionPointIn<T> input) {
        this.graph.connect(output.getOutputVar(), input.getInputVar());
        Connection conn = new NodeConnection(output, input);
        this.add(conn, CONNECTION_LAYER);
    }

    public void createNode(Component parent, int x, int y, Class<? extends Enum<?>>[] filter, Consumer<NodePanel> onSelect) {
        this.popupLocation = SwingUtilities.convertPoint(parent, new Point(x, y), this);

        new NewNodePopup(filter).show(parent, x, y, (selected) -> {
            System.out.println(selected);
            if (selected instanceof TriggerType) {
                System.out.println("create alert node");
                TriggerNode triggerNode = new TriggerNode();
                this.graph.add(triggerNode);
                ChatAlert alert = new ChatAlert();
                triggerNode.setAlert(alert);

                NodePanel nodePanel = new AlertNodePanel(GraphPanel.this, GraphPanel.this.popupLocation.x, GraphPanel.this.popupLocation.y, ((TriggerType) selected).getName(), Color.CYAN, triggerNode);
                GraphPanel.this.add(nodePanel, NODE_LAYER, 0);
                onSelect.accept(nodePanel);
            } else if (selected instanceof NotificationType) {
                System.out.println("create notification node");
                NotificationNode notificationNode = new NotificationNode();
                this.graph.add(notificationNode);
                TextToSpeech tts = new TextToSpeech();
                notificationNode.setNotification(tts);

                NodePanel nodePanel = new NotificationNodePanel(GraphPanel.this, GraphPanel.this.popupLocation.x, GraphPanel.this.popupLocation.y, ((NotificationType) selected).getName(), Color.ORANGE, notificationNode, colorPickerManager);
                GraphPanel.this.add(nodePanel, NODE_LAYER, 0);
                onSelect.accept(nodePanel);
            } else if (selected instanceof LogicNodeType) {
                LogicNodeType logicNodeType = (LogicNodeType) selected;
                switch (logicNodeType) {
                    case AND:
                    case OR:
                    case GREATER_THAN:
                    case LESS_THAN:
                    case EQUALS:
                    case NOT_EQUALS:
                        And andNode = new And();
                        this.graph.add(andNode);
                        NodePanel nodePanel = new IfNodePanel(GraphPanel.this, andNode, GraphPanel.this.popupLocation.x, GraphPanel.this.popupLocation.y, logicNodeType.getName(), Color.MAGENTA);
                        GraphPanel.this.add(nodePanel, NODE_LAYER, 0);
                        onSelect.accept(nodePanel);
                        break;
                }
            }
            GraphPanel.this.revalidate();
            GraphPanel.this.repaint();
        });
    }

    public void onNodeMoved(NodePanel nodePanel) {
        for (Connection connection : nodePanel.getConnections()) {
            connection.recalculateBounds();
        }
    }

    public void moveNodeToTop(NodePanel nodePanel) {
        this.setLayer(nodePanel, NODE_LAYER, 0);
        this.revalidate();
        this.repaint();
    }

    public void removeNode(NodePanel nodePanel) {
        this.remove(nodePanel);
        Arrays.stream(this.getComponentsInLayer(CONNECTION_LAYER))
            .filter(component -> component instanceof NodeConnection)
            .map(component -> (NodeConnection) component)
            .filter(conn -> conn.getStartPoint().getNodePanel() == nodePanel || conn.getEndPoint().getNodePanel() == nodePanel)
            .forEach(this::remove);
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D)g).scale(this.zoomLevel, this.zoomLevel);
        int width = this.getWidth();
        int height = this.getHeight();
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
