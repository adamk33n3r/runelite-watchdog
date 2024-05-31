package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarOutput;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
public class ConnectionPointOut<T> extends ConnectionPoint {
    private VarOutput<T> outputVar;
    private DragConnection newConnection;

    public ConnectionPointOut(NodePanel nodePanel, String name, Class<T> type, T initialValue) {
        super(nodePanel);
        this.outputVar = new VarOutput<>(nodePanel.getNode(), name, type, initialValue);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.print("mouse pressed: ");
                System.out.println(ConnectionPointOut.this);
                Point point = SwingUtilities.convertPoint(ConnectionPointOut.this, e.getPoint(), nodePanel);
                point.x -= NodePanel.PANEL_WIDTH;
                ConnectionPointOut.this.newConnection = new DragConnection(nodePanel, ConnectionPointOut.this, point);
                nodePanel.getGraphPanel().add(ConnectionPointOut.this.newConnection, 0);
                nodePanel.getGraphPanel().revalidate();
                nodePanel.getGraphPanel().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("mouse released");
                System.out.println(ConnectionPointOut.this.newConnection);
                nodePanel.getGraphPanel().remove(ConnectionPointOut.this.newConnection);
                nodePanel.getGraphPanel().revalidate();
                nodePanel.getGraphPanel().repaint();
                ConnectionPointOut.this.newConnection = null;

                Point point = SwingUtilities.convertPoint(ConnectionPointOut.this, e.getPoint(), nodePanel.getGraphPanel());
                Component deepestComponentAt = SwingUtilities.getDeepestComponentAt(nodePanel.getGraphPanel(), point.x, point.y);
                System.out.print("deepest component: ");
                System.out.println(deepestComponentAt);
                if (deepestComponentAt.equals(nodePanel.getGraphPanel())) {
                    System.out.println("dropped on graph");
                    nodePanel.getGraphPanel().createNode(e.getComponent(), e.getX(), e.getY(), new Class[]{NotificationType.class, LogicNodeType.class},(newNode) -> {
                        if (newNode instanceof NotificationNodePanel) {
                            nodePanel.getGraphPanel().connect(((AlertNodePanel)nodePanel).getOutputConnectionPoint(), ((NotificationNodePanel) newNode).getInputConnectionPoint());
                        }
                    });
                    return;
                }

                ConnectionPointIn<?> droppedNode = (ConnectionPointIn<?>) SwingUtilities.getAncestorOfClass(ConnectionPointIn.class, deepestComponentAt);
                if (droppedNode == null) {
                    return;
                }
                // Disallow connecting of incompatible types
                if (droppedNode.getInputVar().getType() != ConnectionPointOut.this.outputVar.getType()) {
                    return;
                }
                // This is ok since we checked above
                @SuppressWarnings("unchecked")
                ConnectionPointIn<T> casted = (ConnectionPointIn<T>) droppedNode;
                System.out.println(point);
                System.out.println(casted);
                System.out.println(casted.getName());
                nodePanel.getGraphPanel().connect(ConnectionPointOut.this, casted);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (ConnectionPointOut.this.newConnection == null)
                    return;
                Point point = e.getPoint();
                point = SwingUtilities.convertPoint(ConnectionPointOut.this, point, nodePanel);
//                point = SwingUtilities.convertPoint(ConnectionPointOut.this, point, node);
                point.x -= NodePanel.PANEL_WIDTH;
//                SwingUtilities.convertPointToScreen(point, ConnectionPointOut.this);
                ConnectionPointOut.this.newConnection.setEndOffset(point);
                ConnectionPointOut.this.newConnection.repaint();
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }
}
