package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.NodeConnection;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodePanel extends JPanel {
    public static final int PANEL_WIDTH = 300;
    public static final int PANEL_HEIGHT = 200;
    public static final int TITLE_HEIGHT = 20;
    public static final Color NODE_BODY_COLOR = new Color(38, 38, 38);
    private static final int CORNER_ARC = 8;

    protected JPanel items;

    @Getter
    private List<NodeConnection> connections = new ArrayList<>();
    private final List<Runnable> disposers = new ArrayList<>();
    @Getter
    private final GraphPanel graphPanel;
    @Getter
    private final Node node;

    private final Map<VarOutput<?>, ConnectionPointOut<?>> outputRegistry = new IdentityHashMap<>();
    // LinkedHashMap preserves insertion order so "first of type" fallback in ConnectionAutoMatcher is deterministic
    private final Map<VarInput<?>, ConnectionPointIn<?>> inputRegistry = new LinkedHashMap<>();

    public void registerOutputPoint(VarOutput<?> var, ConnectionPointOut<?> cp) {
        outputRegistry.put(var, cp);
    }

    public void registerInputPoint(VarInput<?> var, ConnectionPointIn<?> cp) {
        inputRegistry.put(var, cp);
    }

    @SuppressWarnings("unchecked")
    public <T> ConnectionPointOut<T> getOutputPoint(VarOutput<T> var) {
        return (ConnectionPointOut<T>) outputRegistry.get(var);
    }

    @SuppressWarnings("unchecked")
    public <T> ConnectionPointIn<T> getInputPoint(VarInput<T> var) {
        return (ConnectionPointIn<T>) inputRegistry.get(var);
    }

    public Collection<ConnectionPointIn<?>> getAllInputPoints() {
        return Collections.unmodifiableCollection(inputRegistry.values());
    }

    @Getter
    private final Color titleColor;
    private final String typeName;
    private final JLabel nameLabel;
    private final Border border;

//    protected JPanel inConnectionPoints;
//    protected JPanel outConnectionPoints;

    public NodePanel(GraphPanel graphPanel, Node node, int x, int y, String name, Color color) {
        this.graphPanel = graphPanel;
        this.node = node;
        this.titleColor = color;
        this.typeName = name;
        this.setName(name);
        this.setBounds(x, y, PANEL_WIDTH, PANEL_HEIGHT);
        this.setOpaque(false);
        this.border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        this.setLayout(new BorderLayout());
        this.nameLabel = new JLabel(name);
//        nameLabel.setForeground(Util.textColorForBG(color));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setPreferredSize(new Dimension(0, TITLE_HEIGHT));
        nameLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        JButton button = new JButton("\u00D7");
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Util.textColorForBG(color));
        button.setPreferredSize(new Dimension(20, 20));
        button.addActionListener((ev) -> this.graphPanel.removeNode(this));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(180, 40, 40, 160));
                button.setOpaque(true);
                button.setContentAreaFilled(true);
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.repaint();
            }
        });
        topPanel.add(button, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);

        MouseAdapter onTopAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                graphPanel.moveNodeToTop(NodePanel.this);
            }
        };

        JPanel itemsWrapper = new JPanel(new BorderLayout());
        itemsWrapper.setOpaque(false);
        this.items = new JPanel();
        this.items.setOpaque(false);
        itemsWrapper.add(this.items, BorderLayout.NORTH);
        this.items.setBorder(this.border);
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Dark node body
        g2.setColor(NODE_BODY_COLOR);
        g2.fillRoundRect(0, 0, w, h, CORNER_ARC, CORNER_ARC);

        // Colored title bar — clip to the title region so only the top corners round
        Shape originalClip = g2.getClip();
        g2.setClip(0, 0, w, TITLE_HEIGHT);
        g2.setColor(this.titleColor);
        g2.fillRoundRect(0, 0, w, h, CORNER_ARC, CORNER_ARC);
        g2.setClip(originalClip);

        // Border
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(this.titleColor.darker());
        g2.drawRoundRect(0, 0, w - 1, h - 1, CORNER_ARC, CORNER_ARC);

        g2.dispose();
    }

    public void pack() {
        SwingUtilities.invokeLater(() -> {
            int totalHeight = Arrays.stream(this.items.getComponents())
                .mapToInt(c -> c.getPreferredSize().height)
                .sum();
            int padding = 5 * (this.items.getComponentCount() - 1);
            Insets borderInsets = this.border.getBorderInsets(this);
            this.setBounds(this.getX(), this.getY(), PANEL_WIDTH + borderInsets.left + borderInsets.right, totalHeight + TITLE_HEIGHT + padding + 2 + borderInsets.top + borderInsets.bottom); // idk why it's +2
            // Not sure why alert node panels need these but notification node panels don't
            this.revalidate();
            this.repaint();
        });
    }

    public void updateHeaderLabel(String newName) {
        if (newName == null || newName.isBlank()) {
            this.nameLabel.setText(this.typeName);
        } else {
            this.nameLabel.setText(newName + " - " + this.typeName);
        }
    }

    protected void notifyChange() {
        this.graphPanel.notifyChange();
    }

    protected final void watchDirty(VarInput<?>... vars) {
        for (VarInput<?> v : vars) {
            this.addDisposer(v.onChange(_x -> this.notifyChange()));
        }
    }

    public void addConnection(NodeConnection connection) {
        this.connections.add(connection);
    }

    public void removeConnection(NodeConnection connection) {
        this.connections.remove(connection);
    }

    protected void addDisposer(Runnable disposer) {
        this.disposers.add(disposer);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.disposers.forEach(Runnable::run);
        this.disposers.clear();
    }
}
