package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.NodeConnection;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class NodePanel extends JPanel {
    public static final int PANEL_WIDTH = 300;
    public static final int PANEL_HEIGHT = 200;
    public static final int TITLE_HEIGHT = 20;
    protected JPanel items;
    private List<NodeConnection> connections = new ArrayList<>();
    private Color color;
    private GraphPanel graphPanel;
    private Node node;

//    protected JPanel inConnectionPoints;
//    protected JPanel outConnectionPoints;

    public NodePanel(GraphPanel graphPanel, Node node, int x, int y, String name, Color color) {
        this.graphPanel = graphPanel;
        this.node = node;
        this.color = color;
        this.setName(name);
        this.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        this.setBackground(color);
//        this.setBorder(BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR, 2));
//        this.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
        this.setLayout(new BorderLayout());
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Util.textColorForBG(color));
        nameLabel.setPreferredSize(new Dimension(0, TITLE_HEIGHT));
        nameLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(null);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        JButton button = new JButton("X");
        button.addActionListener((ev) -> {
            this.graphPanel.removeNode(this);
        });
        topPanel.add(button, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);

        MouseAdapter onTopAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("mouse pressed node");
                graphPanel.moveNodeToTop(NodePanel.this);
            }
        };

        JPanel itemsWrapper = new JPanel(new BorderLayout());
        this.items = new JPanel();
        itemsWrapper.add(this.items, BorderLayout.NORTH);
        this.items.setLayout(new StretchedStackedLayout(5));
        this.add(itemsWrapper);

        DraggingMouseAdapter draggingMouseAdapter = new DraggingMouseAdapter((start, point) -> {
            point = SwingUtilities.convertPoint(NodePanel.this, point, graphPanel);
            point.translate(-start.x, -start.y);
            point.setLocation(Math.min(Math.max(point.x, 0), graphPanel.getWidth() - PANEL_WIDTH), Math.min(Math.max(point.y, 0), graphPanel.getHeight() - PANEL_HEIGHT));
            this.setLocation(point);
            graphPanel.onNodeMoved(this);
        });
        this.addMouseListener(draggingMouseAdapter);
        this.addMouseMotionListener(draggingMouseAdapter);
        this.addMouseListener(onTopAdapter);
    }

    public void pack() {
        SwingUtilities.invokeLater(() -> {
            int totalHeight = Arrays.stream(this.items.getComponents())
                .map(Component::getHeight)
                .reduce(0, Integer::sum);
            int padding = 5 * (this.items.getComponentCount() - 1);
            this.setBounds(this.getX(), this.getY(), PANEL_WIDTH, totalHeight + TITLE_HEIGHT + padding + 2); // idk why it's +2
        });
    }

    public void addConnection(NodeConnection connection) {
        this.connections.add(connection);
    }

    public void removeConnection(NodeConnection connection) {
        this.connections.remove(connection);
    }
}
