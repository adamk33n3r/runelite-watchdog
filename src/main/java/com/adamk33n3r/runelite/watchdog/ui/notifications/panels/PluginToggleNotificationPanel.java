package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.PluginToggle;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.util.Comparator;

@Slf4j
public class PluginToggleNotificationPanel extends NotificationContentPanel<PluginToggle> {
    private final PluginManager pluginManager;

    public PluginToggleNotificationPanel(PluginToggle notification, PluginManager pluginManager, Runnable onChange) {
        super(notification, onChange);
        this.pluginManager = pluginManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        JComboBox<PluginToggle.ToggleMode> modeSelect = PanelUtils.createSelect(PluginToggle.ToggleMode.values(), this.notification.getMode(), selected -> {
            this.notification.setMode(selected);
            this.onChange.run();
            this.rebuild();
        });
        JPanel mode = PanelUtils.createLabeledComponent("Mode", "The toggle mode", modeSelect);
        mode.setBorder(null);
        mode.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(mode);

        Plugin[] plugins = this.pluginManager.getPlugins()
            .stream().sorted(Comparator.comparing(Plugin::getName))
            .toArray(Plugin[]::new);
        Plugin selectedPlugin = this.notification.getPluginName() == null ? null : this.pluginManager.getPlugins().stream()
            .filter(p -> p.getName().equals(this.notification.getPluginName()))
            .findFirst()
            .orElse(null);
        JComboBox<Plugin> pluginSelect = PanelUtils.createSelect(plugins, selectedPlugin, Plugin::getName, "Select a plugin...", selected -> {
            log.debug("Setting plugin to toggle to {}", selected.getName());
            this.notification.setPluginName(selected.getName());
        });
        this.add(pluginSelect);
    }
}
