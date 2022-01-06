package com.adamk33n3r.runelite.watchdog.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.IdleAlert;
import net.runelite.client.ui.MultiplexingPluginPanel;

public class IdleAlertPanel extends AlertPanel {
    public IdleAlertPanel(IdleAlert alert, MultiplexingPluginPanel muxer, WatchdogPlugin plugin) {
        super(alert);
    }
}
