package com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections;

import com.adamk33n3r.runelite.watchdog.ui.nodegraph.NodePanel;
import lombok.Getter;

import javax.swing.JComponent;
import java.awt.*;

@Getter
public abstract class ConnectionPoint extends JComponent {
    private final NodePanel nodePanel;

    public ConnectionPoint(NodePanel nodePanel) {
        this.nodePanel = nodePanel;
        this.setBackground(Color.RED);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension size = this.getSize();
        g.setColor(this.getBackground());
        g.fillRect(0, 0, size.width, size.height);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }
}
