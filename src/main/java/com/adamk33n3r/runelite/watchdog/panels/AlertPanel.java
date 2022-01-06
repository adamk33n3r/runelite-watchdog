package com.adamk33n3r.runelite.watchdog.panels;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import net.runelite.client.ui.PluginPanel;

import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class AlertPanel extends PluginPanel {
    protected Alert alert;

    protected AlertPanel(Alert alert) {
        this.alert = alert;
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
}
