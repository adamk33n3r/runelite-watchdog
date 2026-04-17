package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.nodegraph.ExecSignal;
import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodeTypeCompatibilityChecker;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ConnectionPointOut<T> extends ConnectionPoint {
    private final VarOutput<T> outputVar;
    private DragConnection newConnection;

    public ConnectionPointOut(NodePanel nodePanel, VarOutput<T> outputVar) {
        super(nodePanel, outputVar.getType() == ExecSignal.class, true, outputVar.getType());
        this.outputVar = outputVar;
        nodePanel.registerOutputPoint(outputVar, this);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
//                System.out.print("mouse pressed: ");
//                System.out.println(ConnectionPointOut.this);
                Point point = SwingUtilities.convertPoint(ConnectionPointOut.this, e.getPoint(), nodePanel);
                point.x -= NodePanel.PANEL_WIDTH;
                ConnectionPointOut.this.newConnection = new DragConnection(nodePanel, ConnectionPointOut.this, point);
                nodePanel.getGraphPanel().setActiveDragType(outputVar.getType());
                nodePanel.getGraphPanel().add(ConnectionPointOut.this.newConnection, GraphPanel.NEW_CONNECTION_LAYER, 0);
                nodePanel.getGraphPanel().revalidate();
                nodePanel.getGraphPanel().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
//                System.out.println("mouse released");
//                System.out.println(ConnectionPointOut.this.newConnection);

                try {
                    Point point = SwingUtilities.convertPoint(ConnectionPointOut.this, e.getPoint(), nodePanel.getGraphPanel());
                    Component deepestComponentAt = PanelUtils.getDeepestComponentAt(nodePanel.getGraphPanel(), GraphPanel.NODE_LAYER, point.x, point.y);
                    //                System.out.print("deepest component: ");
                    //                System.out.println(deepestComponentAt);
                    if (deepestComponentAt.equals(nodePanel.getGraphPanel()) || (deepestComponentAt instanceof Connection && deepestComponentAt.getParent().equals(nodePanel.getGraphPanel()))) {
                        nodePanel.getGraphPanel().createNode(
                            e.getComponent(), e.getX(), e.getY(), outputVar,
                            (newNode) -> {
                                if (newNode != null) {
                                    List<VarInput<?>> candidateVars = newNode.getAllInputPoints().stream()
                                        .map(ConnectionPointIn::getInputVar)
                                        .collect(Collectors.toList());
                                    VarInput<T> bestVar = ConnectionAutoMatcher.findBestMatchingInput(outputVar, candidateVars);
                                    if (bestVar != null) {
                                        ConnectionPointIn<T> bestCp = newNode.getInputPoint(bestVar);
                                        if (bestCp != null) {
                                            nodePanel.getGraphPanel().connect(ConnectionPointOut.this, bestCp);
                                        }
                                    }
                                }
                                removeNewConnection();
                            }
                        );
                        return;
                    }

                    if (deepestComponentAt instanceof ConnectionPointIn) {
                        removeNewConnection();
                        ConnectionPointIn<?> droppedNode = (ConnectionPointIn<?>) deepestComponentAt;
                        // Disallow connecting of incompatible types
                        if (!droppedNode.getInputVar().getType().isAssignableFrom(ConnectionPointOut.this.outputVar.getType())) {
                            System.err.print("Incompatible connection points: ");
                            System.err.println(ConnectionPointOut.this.outputVar.getType() + " -> " + droppedNode.getInputVar().getType());
                            return;
                        }
                        // Disallow connecting to the same node
                        if (droppedNode.getNodePanel().equals(ConnectionPointOut.this.getNodePanel())) {
                            System.err.println("Cannot connect to the same node");
                            return;
                        }
                        // This is ok since we checked above
                        @SuppressWarnings("unchecked")
                        ConnectionPointIn<T> casted = (ConnectionPointIn<T>) droppedNode;
                        nodePanel.getGraphPanel().connect(ConnectionPointOut.this, casted);
                        return;
                    }

                    removeNewConnection();

                } catch (Exception ex) {
                    log.error("e: ", ex);
                    removeNewConnection();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (ConnectionPointOut.this.newConnection == null)
                    return;
                Point point = e.getPoint();
                point = SwingUtilities.convertPoint(ConnectionPointOut.this, point, nodePanel);
                point.x -= NodePanel.PANEL_WIDTH;
                point.x -= 2;
                ConnectionPointOut.this.newConnection.setEndOffset(point);
                ConnectionPointOut.this.newConnection.repaint();
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    @Override
    protected boolean shouldFill() {
        if (this.newConnection != null) return true;
        if (getNodePanel().getGraphPanel().getActiveDragType() != null) return false;
        return this.hovered;
    }

    private void removeNewConnection() {
        if (this.newConnection == null) {
            return;
        }
        this.getNodePanel().getGraphPanel().setActiveDragType(null);
        this.getNodePanel().getGraphPanel().remove(this.newConnection);
        this.getNodePanel().getGraphPanel().revalidate();
        this.getNodePanel().getGraphPanel().repaint();
        this.newConnection = null;
    }
}
