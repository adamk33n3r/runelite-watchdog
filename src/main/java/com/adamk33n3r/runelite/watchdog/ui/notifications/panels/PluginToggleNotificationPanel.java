package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.PluginToggle;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.util.Comparator;

@Slf4j
public class PluginToggleNotificationPanel extends NotificationPanel {
    private final PluginManager pluginManager;
    public PluginToggleNotificationPanel(PluginToggle notification, NotificationsPanel parentPanel, PluginManager pluginManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.pluginManager = pluginManager;

        this.rebuild();
    }

    private void rebuild() {
        this.settings.removeAll();

        PluginToggle notification = (PluginToggle) this.notification;

        JComboBox<PluginToggle.ToggleMode> modeSelect = PanelUtils.createSelect(PluginToggle.ToggleMode.values(), notification.getMode(), (selected) -> {
            notification.setMode(selected);
            onChangeListener.run();
            this.rebuild();
        });
        JPanel mode = PanelUtils.createLabeledComponent("Mode", "The toggle mode", modeSelect);
        mode.setBorder(null);
        mode.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(mode);

        Plugin[] plugins = this.pluginManager.getPlugins()
            .stream().sorted(Comparator.comparing(Plugin::getName))
            .toArray(Plugin[]::new);
        Plugin selectedPlugin = notification.getPluginName() == null ? null : this.pluginManager.getPlugins().stream()
            .filter(p -> p.getName().equals(notification.getPluginName()))
            .findFirst()
            .orElse(null);
        JComboBox<Plugin> pluginSelect = PanelUtils.createSelect(plugins, selectedPlugin, Plugin::getName, "Select a plugin...", (selected) -> {
            log.debug("Setting plugin to toggle to {}", selected.getName());
            notification.setPluginName(selected.getName());
        });
        this.settings.add(pluginSelect);
    }
}
