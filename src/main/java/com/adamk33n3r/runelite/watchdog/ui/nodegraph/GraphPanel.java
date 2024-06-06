package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.nodegraph.nodes.logic.And;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.Connection;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.NodeConnection;
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
import java.util.List;
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

    public void init() {
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(6000, 4000));
        this.setName("Graph");

        /*
         * Alerts
         */

        ChatAlert alert = new ChatAlert("Test Chat Alert");
        alert.setMessage("This is a test message");
        ScreenFlash screenFlash = new ScreenFlash();
        TextToSpeech tts = new TextToSpeech();
//        tts.setSource(TTSSource.ELEVEN_LABS);

        SpawnedAlert spawnedAlert = new SpawnedAlert("Spawned Alert");
        spawnedAlert.setPattern("Henry");
        spawnedAlert.setSpawnedDespawned(SpawnedAlert.SpawnedDespawned.SPAWNED);
        spawnedAlert.setSpawnedType(SpawnedAlert.SpawnedType.NPC);




        /*
         * Nodes
         */
        this.graph = new Graph();
        TriggerNode triggerNode = new TriggerNode(alert);
        this.graph.add(triggerNode);
        NotificationNode notificationNode = new NotificationNode(tts);
        this.graph.add(notificationNode);
        NotificationNode screenFlashNode = new NotificationNode(screenFlash);
        this.graph.add(screenFlashNode);
        Bool boolNode = new Bool();
        boolNode.setValue(false);
        this.graph.add(boolNode);

        Num numNode = new Num();
        numNode.setValue(27);
        this.graph.add(numNode);


        /*
         * Node Panels
         */
        AlertNodePanel test = new AlertNodePanel(this, 425, 165, "Test", Color.RED, triggerNode);
        this.add(test, NODE_LAYER);

        NotificationNodePanel testTEST = new NotificationNodePanel(this, 815, 365, "TestTEST", Color.PINK, notificationNode, colorPickerManager);
        this.add(testTEST, NODE_LAYER);

        NotificationNodePanel screenFlashNodePanel = new NotificationNodePanel(this, 815, 65, "Screen Flash", Color.PINK, screenFlashNode, colorPickerManager);
        this.add(screenFlashNodePanel, NODE_LAYER);

        BoolNodePanel boolNodePanel = new BoolNodePanel(this, boolNode, 15, 15, "Bool Node", Color.CYAN);
        this.add(boolNodePanel, NODE_LAYER);

        NumberNodePanel numNodePanel = new NumberNodePanel(this, numNode, 15, 215, "Num Node", Color.CYAN);
        this.add(numNodePanel);

//        NodePanel logicNodePanel = new IfNodePanel(this, null, 400, 200, "If Node", Color.CYAN);
//        this.add(logicNodePanel, NODE_LAYER);

        this.connect(test.getCaptureGroupsOut(), testTEST.getCaptureGroupsIn());
        this.connect(test.getAlertName(), testTEST.getAlertNameIn());
        this.connect(test.getCaptureGroupsOut(), screenFlashNodePanel.getCaptureGroupsIn());
        this.connect(boolNodePanel.getBoolValue(), screenFlashNodePanel.getEnabledIn());


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
        boolean madeConnection = this.graph.connect(output.getOutputVar(), input.getInputVar());
        if (madeConnection) {
            this.removeConnectionTo(input);
            Connection conn = new NodeConnection(output, input);
            this.add(conn, CONNECTION_LAYER);
//            this.processNode(output.getNodePanel().getNode());
        } else {
            this.graph.disconnect(output.getOutputVar(), input.getInputVar());
            Optional<NodeConnection> first = output.getNodePanel().getConnections().stream().filter(c -> c.getEndPoint().equals(input)).findFirst();
            first.ifPresent(nc -> {
                nc.remove();
                this.remove(nc);
            });
        }
    }

    private void removeConnectionTo(ConnectionPointIn<?> input) {
        Arrays.stream(this.getComponentsInLayer(CONNECTION_LAYER))
            .filter(component -> component instanceof NodeConnection)
            .map(component -> (NodeConnection) component)
            .filter(conn -> conn.getEndPoint().equals(input))
            .forEach(nc -> {
                nc.remove();
                this.remove(nc);
            });
    }

    public void createNode(Component parent, int x, int y, Class<? extends Enum<?>>[] filter, Consumer<NodePanel> onSelect) {
        this.popupLocation = SwingUtilities.convertPoint(parent, new Point(x, y), this);

        new NewNodePopup(filter).show(parent, x, y, (selected) -> {
            System.out.println(selected);
            if (selected instanceof TriggerType) {
                System.out.println("create alert node");
                ChatAlert alert = new ChatAlert();
                TriggerNode triggerNode = new TriggerNode(alert);
                this.graph.add(triggerNode);

                NodePanel nodePanel = new AlertNodePanel(GraphPanel.this, GraphPanel.this.popupLocation.x, GraphPanel.this.popupLocation.y, ((TriggerType) selected).getName(), Color.CYAN, triggerNode);
                GraphPanel.this.add(nodePanel, NODE_LAYER, 0);
                onSelect.accept(nodePanel);
            } else if (selected instanceof NotificationType) {
                System.out.println("create notification node");
                TextToSpeech tts = new TextToSpeech();
                NotificationNode notificationNode = new NotificationNode(tts);
                this.graph.add(notificationNode);

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
        }, () -> onSelect.accept(null));
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
        this.graph.remove(nodePanel.getNode());
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

    public void processNode(Node node) {
        this.graph.process(node);
    }

    public void trigger(TriggerNode triggerNode) {
        this.graph.process(triggerNode);
        List<NotificationNode> reachableNotificationsFromTrigger = this.graph.getReachableNotificationsFromTrigger(triggerNode);
        System.out.println("Will fire the following notifications:");
        for (NotificationNode notificationNode : reachableNotificationsFromTrigger) {
            System.out.println("  " + notificationNode.getNotification().getType().getName());
//            notificationNode.fire();
        }
    }
}
