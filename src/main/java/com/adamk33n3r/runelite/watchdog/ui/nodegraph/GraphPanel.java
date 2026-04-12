package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.nodes.ContinuousTriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Num;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.LocationAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.NotificationNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.Bool;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.Connection;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.NodeConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ImageUtil;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class GraphPanel extends JLayeredPane {
    private static final double MIN_ZOOM = 0.6;
    private static final double MAX_ZOOM = 1.0;
    private static final double OVERVIEW_THRESHOLD = 1.0;
    private static final int CANVAS_WIDTH = 6000;
    private static final int CANVAS_HEIGHT = 4000;
    private static final Color OVERVIEW_EXEC_COLOR = new Color(255, 165, 50);
    public static final Integer NODE_LAYER = 1;
    public static final Integer CONNECTION_LAYER = 0;
    public static final Integer NEW_CONNECTION_LAYER = 2;
    private static final BufferedImage BACKGROUND_IMG = ImageUtil.loadImageResource(GraphPanel.class, "graph-bg.png");
    private double zoomLevel = 1.0;
    private boolean overviewMode = false;
    private Point popupLocation;
    @Getter
    private Graph graph;
    @Getter
    private Class<?> activeDragType = null;

    public void setActiveDragType(Class<?> type) {
        this.activeDragType = type;
        this.repaint();
    }
    private Injector injector;
    private Runnable onChangeCallback;
    private final javax.swing.Timer saveDebounceTimer = new javax.swing.Timer(300, e -> {
        if (this.onChangeCallback != null) this.onChangeCallback.run();
    });

    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    private com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory notificationPanelFactory;

    @Inject
    private com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanelContentFactory alertPanelContentFactory;

    private void setUpExample1() {
        /*
         * Alerts
         */

        ChatAlert alert = new ChatAlert("Test Chat Alert");
        alert.setMessage("This is a test message");
        ScreenFlash screenFlash = new ScreenFlash();
        TextToSpeech tts = new TextToSpeech();
        tts.setDelayMilliseconds(37);
//        tts.setSource(TTSSource.ELEVEN_LABS);

        SpawnedAlert spawnedAlert = new SpawnedAlert("Spawned Alert");
        spawnedAlert.setPattern("Henry");
        spawnedAlert.setSpawnedDespawned(SpawnedAlert.SpawnedDespawned.SPAWNED);
        spawnedAlert.setSpawnedType(SpawnedAlert.SpawnedType.NPC);




        /*
         * Nodes
         */
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
        AlertNodePanel test = new AlertNodePanel(this, 425, 165, "Test", Color.RED, triggerNode, alertPanelContentFactory);
        this.add(test, NODE_LAYER);

        NotificationNodePanel testTEST = new NotificationNodePanel(this, 815, 365, "Text to Speech", Color.PINK, notificationNode, notificationPanelFactory);
        this.add(testTEST, NODE_LAYER);

        NotificationNodePanel screenFlashNodePanel = new NotificationNodePanel(this, 815, 65, "Screen Flash", Color.PINK, screenFlashNode, notificationPanelFactory);
        this.add(screenFlashNodePanel, NODE_LAYER);

        BoolNodePanel boolNodePanel = new BoolNodePanel(this, boolNode, 15, 15, "Bool Node", Color.CYAN);
        this.add(boolNodePanel, NODE_LAYER);

        NumberNodePanel numNodePanel = new NumberNodePanel(this, numNode, 15, 215, "Num Node", Color.CYAN);
        this.add(numNodePanel, NODE_LAYER);

        this.connect(test.getExecOut(), testTEST.getExecIn());
//        this.connect(test.getAlertName(), testTEST.getAlertNameIn());
        this.connect(test.getExecOut(), screenFlashNodePanel.getExecIn());
        this.connect(boolNodePanel.getBoolValueOut(), screenFlashNodePanel.getEnabledIn());
    }

    public void setUpExample2() {
        LocationAlert insideNMZ = this.injector.getInstance(LocationAlert.class);
        insideNMZ.shouldFire();
        TriggerNode nmzTriggerNode = new ContinuousTriggerNode(insideNMZ);
        this.graph.add(nmzTriggerNode);
        AlertNodePanel nmzTriggerNodePanel = new AlertNodePanel(this, 15, 15, "Inside NMZ", Color.RED, nmzTriggerNode, alertPanelContentFactory);
        this.add(nmzTriggerNodePanel, NODE_LAYER);

        NotificationNode soundEffectNode = new NotificationNode(new SoundEffect());
        this.graph.add(soundEffectNode);
        NotificationNodePanel soundEffectNodePanel = new NotificationNodePanel(this, 815, 300, "Sound Effect", Color.PINK, soundEffectNode, notificationPanelFactory);
        this.add(soundEffectNodePanel, NODE_LAYER);

        NotificationNode screenFlashNode = new NotificationNode(new ScreenFlash());
        this.graph.add(screenFlashNode);
        NotificationNodePanel screenFlashNodePanel = new NotificationNodePanel(this, 815, 15, "Screen Flash", Color.PINK, screenFlashNode, notificationPanelFactory);
        this.add(screenFlashNodePanel, NODE_LAYER);

        TriggerNode statChangedTriggerNode = new TriggerNode(new StatChangedAlert());
        this.graph.add(statChangedTriggerNode);
        AlertNodePanel statChangedTriggerNodePanel = new AlertNodePanel(this, 425, 15, "Stat Changed", Color.RED, statChangedTriggerNode, alertPanelContentFactory);
        this.add(statChangedTriggerNodePanel, NODE_LAYER);

        this.connect(nmzTriggerNodePanel.getIsTriggered(), statChangedTriggerNodePanel.getEnabled());
        this.connect(statChangedTriggerNodePanel.getExecOut(), soundEffectNodePanel.getExecIn());
        this.connect(statChangedTriggerNodePanel.getExecOut(), screenFlashNodePanel.getExecIn());
    }

    public void init(Injector injector) {
        this.init(injector, new Graph());
//        this.setUpExample2();
    }

    public void setOnChange(Runnable onChange) {
        this.onChangeCallback = onChange;
    }

    public void notifyChange() {
        this.saveDebounceTimer.restart();
    }

    public void init(Injector injector, Graph existingGraph) {
        this.injector = injector;
        this.saveDebounceTimer.setRepeats(false);
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.setName("Graph");

        this.graph = existingGraph;
        this.loadFromGraph(existingGraph);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GraphPanel.this.grabFocus();
                if (overviewMode && SwingUtilities.isLeftMouseButton(e)) {
                    int worldX = (int)(e.getX() / zoomLevel);
                    int worldY = (int)(e.getY() / zoomLevel);
                    for (Component c : getComponentsInLayer(NODE_LAYER)) {
                        if (!(c instanceof NodePanel)) continue;
                        NodePanel np = (NodePanel) c;
                        if (np.getBounds().contains(worldX, worldY)) {
                            setZoomLevel(1.0);
                            scrollToNode(np);
                            return;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!overviewMode && SwingUtilities.isRightMouseButton(e)) {
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
        this.addMouseWheelListener(e -> {
            e.consume();
            double delta = e.getWheelRotation() > 0 ? -0.1 : 0.1;
            setZoomLevel(zoomLevel + delta);
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    log.debug("Dump Panels");
                    log.debug("Nodes:");
                    for (Component component : GraphPanel.this.getComponentsInLayer(NODE_LAYER)) {
                        if (component instanceof NodePanel) {
                            NodePanel nodePanel = (NodePanel) component;
                            log.debug(nodePanel.getName());
                        }
                    }
                    log.debug("Connections:");
                    for (Component component : GraphPanel.this.getComponentsInLayer(CONNECTION_LAYER)) {
                        if (component instanceof NodeConnection) {
                            NodeConnection nodeConnection = (NodeConnection) component;
                            log.debug(nodeConnection.getStartPoint().getNodePanel().getName() + " -> " + nodeConnection.getEndPoint().getNodePanel().getName());
                        }
                    }

                    log.debug("DUMP GRAPH");
                    log.debug(GraphPanel.this.graph.toString());
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
            if (!input.getInputVar().isAllowMultipleConnections()) {
                this.removeConnectionTo(input);
            }
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
        this.notifyChange();
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
            int px = this.popupLocation.x;
            int py = this.popupLocation.y;
            if (selected instanceof TriggerType) {
                TriggerType triggerType = (TriggerType) selected;
                Alert alert;
                try {
                    alert = injector.getInstance(triggerType.getImplClass());
                } catch (Exception e) {
                    alert = new ChatAlert();
                }
                TriggerNode triggerNode = new TriggerNode(alert);
                triggerNode.setX(px);
                triggerNode.setY(py);
                this.graph.add(triggerNode);
                NodePanel nodePanel = new AlertNodePanel(this, px, py, triggerType.getName(), NODE_TRIGGER_COLOR, triggerNode, alertPanelContentFactory);
                this.add(nodePanel, NODE_LAYER, 0);
                onSelect.accept(nodePanel);
            } else if (selected instanceof NotificationType) {
                NotificationType notificationType = (NotificationType) selected;
                com.adamk33n3r.runelite.watchdog.notifications.Notification notification;
                try {
                    notification = injector.getInstance(notificationType.getImplClass());
                } catch (Exception e) {
                    notification = new TextToSpeech();
                }
                NotificationNode notificationNode = new NotificationNode(notification);
                notificationNode.setX(px);
                notificationNode.setY(py);
                this.graph.add(notificationNode);
                NodePanel nodePanel = new NotificationNodePanel(this, px, py, notificationType.getName(), NODE_NOTIFICATION_COLOR, notificationNode, notificationPanelFactory);
                this.add(nodePanel, NODE_LAYER, 0);
                onSelect.accept(nodePanel);
            } else if (selected instanceof LogicNodeType) {
                LogicNodeType logicNodeType = (LogicNodeType) selected;
                switch (logicNodeType) {
                    case BOOLEAN:
                        BooleanGate boolGate = new BooleanGate();
                        boolGate.setX(px);
                        boolGate.setY(py);
                        this.graph.add(boolGate);
                        NodePanel boolGatePanel = new BooleanGateNodePanel(this, boolGate, px, py, logicNodeType.getName(), NODE_LOGIC_COLOR);
                        this.add(boolGatePanel, NODE_LAYER, 0);
                        onSelect.accept(boolGatePanel);
                        break;
                    case EQUALITY:
                        Equality equalityNode = new Equality();
                        equalityNode.setX(px);
                        equalityNode.setY(py);
                        this.graph.add(equalityNode);
                        NodePanel equalityPanel = new EqualityNodePanel(this, equalityNode, px, py, logicNodeType.getName(), NODE_LOGIC_COLOR);
                        this.add(equalityPanel, NODE_LAYER, 0);
                        onSelect.accept(equalityPanel);
                        break;
                }
            } else if (selected instanceof VariableNodeType) {
                VariableNodeType variableNodeType = (VariableNodeType) selected;
                NodePanel nodePanel;
                switch (variableNodeType) {
                    case BOOLEAN:
                        Bool boolNode = new Bool();
                        boolNode.setX(px);
                        boolNode.setY(py);
                        this.graph.add(boolNode);
                        nodePanel = this.createNodePanel(boolNode);
                        this.add(nodePanel, NODE_LAYER, 0);
                        onSelect.accept(nodePanel);
                        break;
                    case NUMBER:
                        Num numNode = new Num();
                        numNode.setX(px);
                        numNode.setY(py);
                        this.graph.add(numNode);
                        nodePanel = this.createNodePanel(numNode);
                        this.add(nodePanel, NODE_LAYER, 0);
                        onSelect.accept(nodePanel);
                        break;
                }
            }
            this.revalidate();
            this.repaint();
            this.notifyChange();
        }, () -> onSelect.accept(null));
    }

    /**
     * Reconstructs the visual representation of a graph by creating NodePanels
     * for each node and NodeConnections for each connection.
     */
    public void loadFromGraph(Graph g) {
        Map<Node, NodePanel> nodePanelMap = new java.util.IdentityHashMap<>();
        for (Node node : g.getNodes()) {
            NodePanel panel = createNodePanel(node);
            if (panel != null) {
                nodePanelMap.put(node, panel);
                this.add(panel, NODE_LAYER);
            }
        }
        for (com.adamk33n3r.nodegraph.Connection<?> conn : g.getConnections()) {
            NodePanel outPanel = nodePanelMap.get(conn.getOutput().getNode());
            NodePanel inPanel = nodePanelMap.get(conn.getInput().getNode());
            if (outPanel == null || inPanel == null) continue;
            ConnectionPointOut<?> outPoint = outPanel.getOutputPoint(conn.getOutput());
            ConnectionPointIn<?> inPoint = inPanel.getInputPoint(conn.getInput());
            if (outPoint == null || inPoint == null) continue;
//            this.connect(outPoint, inPoint);
//            this.graph.connect(outPoint.getOutputVar(), inPoint.getInputVar());
            outPoint.getOutputVar().fireConnectChange(true);
            inPoint.getInputVar().fireConnectChange(true);
            NodeConnection nc = new NodeConnection(outPoint, inPoint);
            this.add(nc, CONNECTION_LAYER);
        }
        this.revalidate();
        this.repaint();
    }

    private static final Color NODE_TRIGGER_COLOR = new java.awt.Color(50, 120, 200);
    private static final Color NODE_NOTIFICATION_COLOR = new java.awt.Color(210, 90, 30);
    private static final Color NODE_CONSTANT_COLOR = new java.awt.Color(50, 165, 50);
    private static final Color NODE_LOGIC_COLOR = new java.awt.Color(145, 60, 210);

    /**
     * Creates the appropriate NodePanel for a given Node instance.
     */
    public NodePanel createNodePanel(Node node) {
        if (node instanceof ContinuousTriggerNode) {
            ContinuousTriggerNode tn = (ContinuousTriggerNode) node;
            String name = tn.getAlert().getName() != null ?
                String.format("%s - %s", tn.getAlert().getName(), tn.getAlert().getType().getName()) :
                tn.getAlert().getType().getName();
            return new AlertNodePanel(this, tn.getX(), tn.getY(), name, NODE_TRIGGER_COLOR, tn, alertPanelContentFactory);
        } else if (node instanceof TriggerNode) {
            TriggerNode tn = (TriggerNode) node;
            String name = tn.getAlert().getName() != null ?
                String.format("%s - %s", tn.getAlert().getName(), tn.getAlert().getType().getName()) :
                tn.getAlert().getType().getName();
            return new AlertNodePanel(this, tn.getX(), tn.getY(), name, NODE_TRIGGER_COLOR, tn, alertPanelContentFactory);
        } else if (node instanceof NotificationNode) {
            NotificationNode nn = (NotificationNode) node;
            String name = nn.getNotification().getType().getName();
            return new NotificationNodePanel(this, nn.getX(), nn.getY(), name, NODE_NOTIFICATION_COLOR, nn, notificationPanelFactory);
        } else if (node instanceof Bool) {
            Bool boolNode = (Bool) node;
            String boolName = !boolNode.getNameOut().getValue().isEmpty() ? boolNode.getNameOut().getValue() : "Bool";
            return new BoolNodePanel(this, boolNode, node.getX(), node.getY(), boolName, NODE_CONSTANT_COLOR);
        } else if (node instanceof Num) {
            Num numNode = (Num) node;
            String numName = !numNode.getNameOut().getValue().isEmpty() ? numNode.getNameOut().getValue() : "Number";
            return new NumberNodePanel(this, numNode, node.getX(), node.getY(), numName, NODE_CONSTANT_COLOR);
        } else if (node instanceof BooleanGate) {
            BooleanGate bg = (BooleanGate) node;
            return new BooleanGateNodePanel(this, bg, bg.getX(), bg.getY(), LogicNodeType.BOOLEAN.getName(), NODE_LOGIC_COLOR);
        } else if (node instanceof Equality) {
            Equality eq = (Equality) node;
            return new EqualityNodePanel(this, eq, eq.getX(), eq.getY(), LogicNodeType.EQUALITY.getName(), NODE_LOGIC_COLOR);
        }
        return null;
    }

    public void onNodeMoved(NodePanel nodePanel) {
        nodePanel.getNode().setX(nodePanel.getX());
        nodePanel.getNode().setY(nodePanel.getY());
        for (Connection connection : nodePanel.getConnections()) {
            connection.recalculateBounds();
        }
        this.notifyChange();
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
        this.notifyChange();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int scaledW = Math.max(1, (int)(BACKGROUND_IMG.getWidth() * zoomLevel));
        int scaledH = Math.max(1, (int)(BACKGROUND_IMG.getHeight() * zoomLevel));
        for (int x = 0; x < getWidth(); x += scaledW) {
            for (int y = 0; y < getHeight(); y += scaledH) {
                g.drawImage(BACKGROUND_IMG, x, y, scaledW, scaledH, this);
            }
        }
        if (overviewMode) {
            paintOverview((Graphics2D) g);
        }
    }

    private void setZoomLevel(double zoom) {
        double newZoom = Math.round(Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom)) * 10.0) / 10.0;
        boolean shouldBeOverview = newZoom < OVERVIEW_THRESHOLD;
        if (shouldBeOverview != this.overviewMode) {
            this.overviewMode = shouldBeOverview;
            for (Component c : this.getComponents()) {
                c.setVisible(!this.overviewMode);
            }
        }
        this.zoomLevel = newZoom;
        if (this.overviewMode) {
            this.setPreferredSize(new Dimension(
                (int)(CANVAS_WIDTH * this.zoomLevel),
                (int)(CANVAS_HEIGHT * this.zoomLevel)
            ));
        } else {
            this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        }
        this.revalidate();
        this.repaint();
    }

    private void scrollToNode(NodePanel np) {
        JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        if (viewport == null) return;
        Rectangle vr = viewport.getViewRect();
        int x = np.getX() - vr.width / 2 + np.getWidth() / 2;
        int y = np.getY() - vr.height / 2 + np.getHeight() / 2;
        scrollRectToVisible(new Rectangle(x, y, vr.width, vr.height));
    }

    private void paintOverview(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform saved = g2.getTransform();
        g2.scale(zoomLevel, zoomLevel);

        for (Component c : getComponentsInLayer(CONNECTION_LAYER)) {
            if (c instanceof NodeConnection) {
                paintConnectionInOverview(g2, (NodeConnection) c);
            }
        }
        for (Component c : getComponentsInLayer(NODE_LAYER)) {
            if (c instanceof NodePanel) {
                paintNodeBodyInOverview(g2, (NodePanel) c);
            }
        }

        // Draw labels at fixed screen size — reset transform so font size is viewport-relative
        g2.setTransform(saved);
        for (Component c : getComponentsInLayer(NODE_LAYER)) {
            if (c instanceof NodePanel) {
                paintNodeLabelInOverview(g2, (NodePanel) c);
            }
        }
    }

    private void paintNodeBodyInOverview(Graphics2D g2, NodePanel np) {
        int x = np.getX(), y = np.getY(), w = np.getWidth(), h = np.getHeight();
        Color titleColor = np.getTitleColor();

        // Dark body — matches live mode
        g2.setColor(NodePanel.NODE_BODY_COLOR);
        g2.fillRoundRect(x, y, w, h, 10, 10);

        // Colored title bar
        g2.setColor(titleColor);
        g2.fillRect(x, y, w, NodePanel.TITLE_HEIGHT + 4);
        g2.fillRoundRect(x, y, w, NodePanel.TITLE_HEIGHT + 14, 10, 10);

        // Border
        g2.setStroke(new BasicStroke(2));
        g2.setColor(titleColor.darker());
        g2.drawRoundRect(x, y, w, h, 10, 10);
    }

    private void paintNodeLabelInOverview(Graphics2D g2, NodePanel np) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        g2.setColor(Util.textColorForBG(np.getTitleColor()));
        int screenX = (int)(np.getX() * zoomLevel) + 7;
        int screenY = (int)(np.getY() * zoomLevel) + 13;
        g2.drawString(np.getName(), screenX, screenY);
    }

    private void paintConnectionInOverview(Graphics2D g2, NodeConnection nc) {
        ConnectionPointOut<?> startPort = nc.getStartPoint();
        ConnectionPointIn<?> endPort = nc.getEndPoint();
        if (startPort == null || endPort == null) return;

        // convertPoint works on hidden components — bounds are preserved by Swing
        Point s = SwingUtilities.convertPoint(startPort.getParent(), startPort.getLocation(), this);
        s.translate(startPort.getWidth(), startPort.getHeight() / 2);
        Point e = SwingUtilities.convertPoint(endPort.getParent(), endPort.getLocation(), this);
        e.translate(0, endPort.getHeight() / 2);

        boolean exec = startPort.isExec();
        g2.setColor(exec ? OVERVIEW_EXEC_COLOR : Color.WHITE);
        g2.setStroke(new BasicStroke(exec ? 3 : 2));
        g2.draw(new CubicCurve2D.Double(s.x, s.y, s.x + 100, s.y, e.x - 100, e.y, e.x, e.y));
    }

    public void processNode(Node node) {
        this.graph.process(node);
    }

    public void trigger(TriggerNode triggerNode, String[] triggerValues) {
        triggerNode.getCaptureGroupsIn().setValue(triggerValues);
        this.graph.process(triggerNode);
        this.graph.getReachableNotificationsFromTrigger(triggerNode)
            .forEach(NotificationNode::fire);
    }
}
