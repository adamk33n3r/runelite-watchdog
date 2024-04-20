package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;

import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;

import lombok.Getter;

public class WatchdogMuxer extends MultiplexingPluginPanel {
    @Getter
    private boolean isActive;

    public WatchdogMuxer(PluginPanel root) {
        super(root);
    }

    @Override
    protected void onAdd(PluginPanel p) {
        // TODO remove if it ever gets fixed https://github.com/runelite/runelite/issues/17712
        if (p instanceof AlertPanel) {
            ((AlertPanel<?>) p).rebuild();
        }
    }

    @Override
    public void onActivate() {
        super.onActivate();
        this.isActive = true;
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        this.isActive = false;
    }
}
