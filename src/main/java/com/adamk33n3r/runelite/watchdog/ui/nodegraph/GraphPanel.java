package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.ContinuousTriggerNode;
import com.adamk33n3r.nodegraph.nodes.constants.*;
import com.adamk33n3r.nodegraph.nodes.logic.BooleanGate;
import com.adamk33n3r.nodegraph.nodes.logic.Equality;
import com.adamk33n3r.nodegraph.nodes.logic.InventoryCheck;
import com.adamk33n3r.nodegraph.nodes.logic.LocationCompare;
import com.adamk33n3r.nodegraph.nodes.math.Add;
import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.TriggerType;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.ChatAlert;
import com.adamk33n3r.runelite.watchdog.alerts.LocationAlert;
import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;

import com.adamk33n3r.nodegraph.Graph;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.ActionNode;
import com.adamk33n3r.nodegraph.nodes.DelayNode;
import com.adamk33n3r.nodegraph.nodes.TriggerNode;
import com.adamk33n3r.runelite.watchdog.alerts.StatChangedAlert;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.Connection;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.NodeConnection;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic.BooleanGateNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic.EqualityNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic.InventoryCheckNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.logic.LocationCompareNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables.*;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanelContentFactory;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationPanelFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.ImageUtil;

import com.google.inject.Injector;

import javax.annotation.Nullable;
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
    private Runnable onChangeCallback;
    private final javax.swing.Timer saveDebounceTimer = new javax.swing.Timer(300, e -> {
        if (this.onChangeCallback != null) this.onChangeCallback.run();
    });

    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    private AlertPanelContentFactory alertPanelContentFactory;

    @Inject
    private NotificationPanelFactory notificationPanelFactory;

    @Inject
    private Injector injector;

    @Inject
    private NodeTypeCompatibilityChecker compatibilityChecker;

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
        ActionNode notificationNode = new ActionNode(tts);
        this.graph.add(notificationNode);
        ActionNode screenFlashNode = new ActionNode(screenFlash);
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

        ActionNodePanel testTEST = new ActionNodePanel(this, 815, 365, "Text to Speech", Color.PINK, notificationNode, notificationPanelFactory);
        this.add(testTEST, NODE_LAYER);

        ActionNodePanel screenFlashNodePanel = new ActionNodePanel(this, 815, 65, "Screen Flash", Color.PINK, screenFlashNode, notificationPanelFactory);
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

        ActionNode soundEffectNode = new ActionNode(new SoundEffect());
        this.graph.add(soundEffectNode);
        ActionNodePanel soundEffectNodePanel = new ActionNodePanel(this, 815, 300, "Sound Effect", Color.PINK, soundEffectNode, notificationPanelFactory);
        this.add(soundEffectNodePanel, NODE_LAYER);

        ActionNode screenFlashNode = new ActionNode(new ScreenFlash());
        this.graph.add(screenFlashNode);
        ActionNodePanel screenFlashNodePanel = new ActionNodePanel(this, 815, 15, "Screen Flash", Color.PINK, screenFlashNode, notificationPanelFactory);
        this.add(screenFlashNodePanel, NODE_LAYER);

        TriggerNode statChangedTriggerNode = new TriggerNode(new StatChangedAlert());
        this.graph.add(statChangedTriggerNode);
        AlertNodePanel statChangedTriggerNodePanel = new AlertNodePanel(this, 425, 15, "Stat Changed", Color.RED, statChangedTriggerNode, alertPanelContentFactory);
        this.add(statChangedTriggerNodePanel, NODE_LAYER);

        this.connect(nmzTriggerNodePanel.getIsTriggered(), statChangedTriggerNodePanel.getEnabled());
        this.connect(statChangedTriggerNodePanel.getExecOut(), soundEffectNodePanel.getExecIn());
        this.connect(statChangedTriggerNodePanel.getExecOut(), screenFlashNodePanel.getExecIn());
    }

    public void init() {
        this.init(new Graph());
//        this.setUpExample2();
    }

    public void setOnChange(Runnable onChange) {
        this.onChangeCallback = onChange;
    }

    public void notifyChange() {
        this.saveDebounceTimer.restart();
    }

    public void init(Graph existingGraph) {
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

    public void createNode(Component parent, int x, int y, @Nullable VarOutput<?> outputVar, Consumer<NodePanel> onSelect) {
        this.popupLocation = SwingUtilities.convertPoint(parent, new Point(x, y), this);

        new NewNodePopup(enumVal -> outputVar == null || this.compatibilityChecker.hasCompatibleInput(enumVal, outputVar.getType()))
            .show(parent, x, y, (selected) -> {
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
                    NodePanel nodePanel = this.createNodePanel(triggerNode, triggerNode.getAlert().getType().getName());
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
                    ActionNode notificationNode = new ActionNode(notification);
                    notificationNode.setX(px);
                    notificationNode.setY(py);
                    this.graph.add(notificationNode);
                    NodePanel nodePanel = this.createNodePanel(notificationNode, notificationNode.getNotification().getType().getName());
                    this.add(nodePanel, NODE_LAYER, 0);
                    onSelect.accept(nodePanel);
                } else if (selected instanceof LogicNodeType) {
                    NodePanel nodePanel;
                    LogicNodeType logicNodeType = (LogicNodeType) selected;
                    switch (logicNodeType) {
                        case BOOLEAN:
                            BooleanGate boolGate = new BooleanGate();
                            boolGate.setX(px);
                            boolGate.setY(py);
                            this.graph.add(boolGate);
                            NodePanel boolGatePanel = this.createNodePanel(boolGate, logicNodeType.getName());
                            this.add(boolGatePanel, NODE_LAYER, 0);
                            onSelect.accept(boolGatePanel);
                            break;
                        case EQUALITY:
                            Equality equalityNode = new Equality();
                            equalityNode.setX(px);
                            equalityNode.setY(py);
                            this.graph.add(equalityNode);
                            NodePanel equalityPanel = this.createNodePanel(equalityNode, logicNodeType.getName());
                            this.add(equalityPanel, NODE_LAYER, 0);
                            onSelect.accept(equalityPanel);
                            break;
                        case LOCATION_COMPARE:
                            LocationCompare locCompare = new LocationCompare();
                            locCompare.setX(px);
                            locCompare.setY(py);
                            this.graph.add(locCompare);
                            NodePanel locComparePanel = this.createNodePanel(locCompare, logicNodeType.getName());
                            this.add(locComparePanel, NODE_LAYER, 0);
                            onSelect.accept(locComparePanel);
                            break;
                        case INVENTORY_CHECK: {
                            InventoryCheck invNode = new InventoryCheck();
                            invNode.setX(px);
                            invNode.setY(py);
                            this.graph.add(invNode);
                            nodePanel = this.createNodePanel(invNode, logicNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
                    }
                } else if (selected instanceof MathNodeType) {
                    MathNodeType mathNodeType = (MathNodeType) selected;
                    switch (mathNodeType) {
                        case ADD:
                            Add addNode = new Add();
                            addNode.setX(px);
                            addNode.setY(py);
                            this.graph.add(addNode);
                            NodePanel addPanel = this.createNodePanel(addNode, mathNodeType.getName());
                            this.add(addPanel, NODE_LAYER, 0);
                            onSelect.accept(addPanel);
                            break;
                    }
                } else if (selected instanceof FlowNodeType) {
                    FlowNodeType flowNodeType = (FlowNodeType) selected;
                    switch (flowNodeType) {
                        case DELAY: {
                            DelayNode delayNode = new DelayNode();
                            delayNode.setX(px);
                            delayNode.setY(py);
                            this.graph.add(delayNode);
                            NodePanel nodePanel = this.createNodePanel(delayNode, flowNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
                    }
                } else if (selected instanceof VariableNodeType) {
                    VariableNodeType variableNodeType = (VariableNodeType) selected;
                    NodePanel nodePanel;
                    switch (variableNodeType) {
                        case BOOLEAN: {
                            Bool boolNode = new Bool();
                            boolNode.setX(px);
                            boolNode.setY(py);
                            this.graph.add(boolNode);
                            nodePanel = this.createNodePanel(boolNode, variableNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
                        case NUMBER: {
                            Num numNode = new Num();
                            numNode.setX(px);
                            numNode.setY(py);
                            this.graph.add(numNode);
                            nodePanel = this.createNodePanel(numNode, variableNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
                        case LOCATION: {
                            Location locNode = new Location();
                            locNode.setX(px);
                            locNode.setY(py);
                            this.graph.add(locNode);
                            nodePanel = this.createNodePanel(locNode, variableNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
                        case PLUGIN: {
                            PluginVar pluginNode = new PluginVar();
                            pluginNode.setX(px);
                            pluginNode.setY(py);
                            this.graph.add(pluginNode);
                            nodePanel = this.createNodePanel(pluginNode, variableNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
                        case INVENTORY: {
                            Inventory invNode = new Inventory();
                            invNode.setX(px);
                            invNode.setY(py);
                            this.graph.add(invNode);
                            nodePanel = this.createNodePanel(invNode, variableNodeType.getName());
                            this.add(nodePanel, NODE_LAYER, 0);
                            onSelect.accept(nodePanel);
                            break;
                        }
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
            NodePanel panel = this.createNodePanel(node, this.getNodeDisplayName(node));
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

    private String getNodeDisplayName(Node node) {
        if (node instanceof TriggerNode) {
            return ((TriggerNode) node).getAlert().getType().getName();
        } else if (node instanceof ActionNode) {
            return ((ActionNode) node).getNotification().getType().getName();
        }
        Class<? extends Node> nodeClass = node.getClass();
        for (VariableNodeType type : VariableNodeType.values()) {
            if (type.getImplClass() == nodeClass) return type.getName();
        }
        for (LogicNodeType type : LogicNodeType.values()) {
            if (type.getImplClass() == nodeClass) return type.getName();
        }
        for (MathNodeType type : MathNodeType.values()) {
            if (type.getImplClass() == nodeClass) return type.getName();
        }
        for (FlowNodeType type : FlowNodeType.values()) {
            if (type.getImplClass() == nodeClass) return type.getName();
        }
        return node.getClass().getSimpleName();
    }

    private static final Color NODE_TRIGGER_COLOR = new java.awt.Color(50, 120, 200);
    private static final Color NODE_ACTION_COLOR = new java.awt.Color(210, 90, 30);
    private static final Color NODE_CONSTANT_COLOR = new java.awt.Color(50, 165, 50);
    private static final Color NODE_LOGIC_COLOR = new java.awt.Color(145, 60, 210);
    private static final Color NODE_MATH_COLOR = new java.awt.Color(219, 195, 34);

    /**
     * Creates the appropriate NodePanel for a given Node instance.
     */
    public NodePanel createNodePanel(Node node, String name) {
        if (node instanceof ContinuousTriggerNode) {
            ContinuousTriggerNode tn = (ContinuousTriggerNode) node;
            return new AlertNodePanel(this, tn.getX(), tn.getY(), name, NODE_TRIGGER_COLOR, tn, alertPanelContentFactory);
        } else if (node instanceof TriggerNode) {
            TriggerNode tn = (TriggerNode) node;
            return new AlertNodePanel(this, tn.getX(), tn.getY(), name, NODE_TRIGGER_COLOR, tn, alertPanelContentFactory);
        } else if (node instanceof ActionNode) {
            ActionNode nn = (ActionNode) node;
            return new ActionNodePanel(this, nn.getX(), nn.getY(), name, NODE_ACTION_COLOR, nn, notificationPanelFactory);
        } else if (node instanceof Bool) {
            Bool boolNode = (Bool) node;
            return new BoolNodePanel(this, boolNode, node.getX(), node.getY(), name, NODE_CONSTANT_COLOR);
        } else if (node instanceof Num) {
            Num numNode = (Num) node;
            return new NumberNodePanel(this, numNode, node.getX(), node.getY(), name, NODE_CONSTANT_COLOR);
        } else if (node instanceof BooleanGate) {
            BooleanGate bg = (BooleanGate) node;
            return new BooleanGateNodePanel(this, bg, bg.getX(), bg.getY(), name, NODE_LOGIC_COLOR);
        } else if (node instanceof Equality) {
            Equality eq = (Equality) node;
            return new EqualityNodePanel(this, eq, eq.getX(), eq.getY(), name, NODE_LOGIC_COLOR);
        } else if (node instanceof Location) {
            Location locNode = (Location) node;
            return new LocationNodePanel(this, locNode, node.getX(), node.getY(), name, NODE_CONSTANT_COLOR);
        } else if (node instanceof PluginVar) {
            PluginVar pvNode = (PluginVar) node;
            return new PluginNodePanel(this, pvNode, node.getX(), node.getY(), name, NODE_CONSTANT_COLOR, injector.getInstance(PluginManager.class));
        } else if (node instanceof Inventory) {
            Inventory invNode = (Inventory) node;
            return new InventoryVariableNodePanel(this, invNode, node.getX(), node.getY(), name, NODE_CONSTANT_COLOR);
        } else if (node instanceof LocationCompare) {
            LocationCompare lc = (LocationCompare) node;
            return new LocationCompareNodePanel(this, lc, lc.getX(), lc.getY(), name, NODE_LOGIC_COLOR, injector.getInstance(Client.class));
        } else if (node instanceof InventoryCheck) {
            InventoryCheck invNode = (InventoryCheck) node;
            return new InventoryCheckNodePanel(this, invNode, node.getX(), node.getY(), name, NODE_LOGIC_COLOR);
        } else if (node instanceof Add) {
            Add addNode = (Add) node;
            return new AddNodePanel(this, addNode, addNode.getX(), addNode.getY(), name, NODE_MATH_COLOR);
        } else if (node instanceof DelayNode) {
            DelayNode delayNode = (DelayNode) node;
            return new DelayNodePanel(this, delayNode.getX(), delayNode.getY(), delayNode);
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
        this.graph.executeExecChain(triggerNode, triggerValues);
    }
}
