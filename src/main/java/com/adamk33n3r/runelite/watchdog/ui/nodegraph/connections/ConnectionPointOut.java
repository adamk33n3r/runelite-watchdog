package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.AlertNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.LogicNodeType;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NotificationNodePanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
public class ConnectionPointOut<T> extends ConnectionPoint {
    private final VarOutput<T> outputVar;
    private DragConnection newConnection;

    public ConnectionPointOut(NodePanel nodePanel, VarOutput<T> outputVar) {
        super(nodePanel);
        this.outputVar = outputVar;
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
//                System.out.print("mouse pressed: ");
//                System.out.println(ConnectionPointOut.this);
                Point point = SwingUtilities.convertPoint(ConnectionPointOut.this, e.getPoint(), nodePanel);
                point.x -= NodePanel.PANEL_WIDTH;
                ConnectionPointOut.this.newConnection = new DragConnection(nodePanel, ConnectionPointOut.this, point);
                nodePanel.getGraphPanel().add(ConnectionPointOut.this.newConnection, GraphPanel.NEW_CONNECTION_LAYER, 0);
                nodePanel.getGraphPanel().revalidate();
                nodePanel.getGraphPanel().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
//                System.out.println("mouse released");
//                System.out.println(ConnectionPointOut.this.newConnection);

                Point point = SwingUtilities.convertPoint(ConnectionPointOut.this, e.getPoint(), nodePanel.getGraphPanel());
                Component deepestComponentAt = PanelUtils.getDeepestComponentAt(nodePanel.getGraphPanel(), GraphPanel.NODE_LAYER, point.x, point.y);
//                System.out.print("deepest component: ");
//                System.out.println(deepestComponentAt);
                if (deepestComponentAt.equals(nodePanel.getGraphPanel()) || (deepestComponentAt instanceof Connection && deepestComponentAt.getParent().equals(nodePanel.getGraphPanel()))) {
//                    System.out.println("dropped on graph");
                    // TODO: Update this to filter to only allow nodes that have a connection point that will match the connection point being dragged from
                    nodePanel.getGraphPanel().createNode(e.getComponent(), e.getX(), e.getY(), new Class[]{NotificationType.class, LogicNodeType.class}, (newNode) -> {
                        if (newNode instanceof NotificationNodePanel) {
                            // TODO: update this to connect the correct connection point after the above
                            nodePanel.getGraphPanel().connect(((AlertNodePanel)nodePanel).getCaptureGroupsOut(), ((NotificationNodePanel) newNode).getCaptureGroupsIn());
                        }
                        removeNewConnection();
                    });
                    return;
                }

                if (deepestComponentAt instanceof ConnectionPointIn) {
                    removeNewConnection();
                    ConnectionPointIn<?> droppedNode = (ConnectionPointIn<?>) deepestComponentAt;
                    // Disallow connecting of incompatible types
                    if (droppedNode.getInputVar().getType() != ConnectionPointOut.this.outputVar.getType()) {
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

    private void removeNewConnection() {
        this.getNodePanel().getGraphPanel().remove(ConnectionPointOut.this.newConnection);
        this.getNodePanel().getGraphPanel().revalidate();
        this.getNodePanel().getGraphPanel().repaint();
        ConnectionPointOut.this.newConnection = null;
    }
}
