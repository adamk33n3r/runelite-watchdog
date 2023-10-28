package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Node extends JPanel {
    public static final int PANEL_WIDTH = 300;
    public static final int PANEL_HEIGHT = 200;
    protected JPanel items;
    private List<NodeConnection> connections = new ArrayList<>();
    private Color color;
    private Graph graph;

    public Node(Graph graph, int x, int y, String name, Color color) {
        this.graph = graph;
        this.color = color;
        this.setName(name);
        this.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        this.setBackground(color);
//        this.setBorder(BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 4));
//        this.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
        this.setLayout(new BorderLayout());
        JLabel nameLabel = new JLabel(name);
        nameLabel.setPreferredSize(new Dimension(0, 20));
        this.add(nameLabel, BorderLayout.NORTH);

        MouseAdapter onTopAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("mouse pressed node");
                graph.moveNodeToTop(Node.this);
            }
        };

        JPanel itemsWrapper = new JPanel(new BorderLayout());
        this.items = new JPanel();
        itemsWrapper.add(this.items, BorderLayout.NORTH);
//        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
        this.items.setLayout(new DynamicGridLayout(0, 1, 5, 5));
        this.add(itemsWrapper);

//        this.items.add(new TextInput("Message", "asdf")).addMouseListener(onTopAdapter);
//        items.add(Box.createVerticalStrut(10));
//        items.add(Box.createVerticalGlue());
//        this.items.add(new NumberInput("X", 8)).addMouseListener(onTopAdapter);
//        Box.Filler verticalGlue = (Box.Filler) Box.createVerticalGlue();
//        verticalGlue.changeShape(verticalGlue.getMinimumSize(), new Dimension(0, Short.MAX_VALUE), verticalGlue.getMaximumSize());
//        items.add(verticalGlue);
//        this.items.add(new NumberInput("Y", 9)).addMouseListener(onTopAdapter);

        DraggingMouseAdapter draggingMouseAdapter = new DraggingMouseAdapter((start, point) -> {
            point = SwingUtilities.convertPoint(Node.this, point, graph);
            point.translate(-start.x, -start.y);
//            setLocation(point.x - start.x, point.y - start.y);
            point.setLocation(Math.min(Math.max(point.x, 0), graph.getWidth() - PANEL_WIDTH), Math.min(Math.max(point.y, 0), graph.getHeight() - PANEL_HEIGHT));
            this.setLocation(point);
            graph.onNodeMoved(this);
        });
        this.addMouseListener(draggingMouseAdapter);
        this.addMouseMotionListener(draggingMouseAdapter);
        this.addMouseListener(onTopAdapter);


    }

    public void addConnection(NodeConnection connection) {
        this.connections.add(connection);
    }
}
