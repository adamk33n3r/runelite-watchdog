package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;

import javax.inject.Inject;
import javax.swing.*;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PluginToggle extends Notification {
    private String pluginName;
    private ToggleMode mode = ToggleMode.TOGGLE;

    @Inject
    private transient PluginManager pluginManager;

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.pluginManager.getPlugins().stream().filter(p -> p.getName().equals(this.pluginName)).findFirst().ifPresent(plugin -> {
            // Plugin start/stop must be done on the EDT
            SwingUtilities.invokeLater(() -> {
                if (this.mode == ToggleMode.ENABLE || (this.mode == ToggleMode.TOGGLE && !this.pluginManager.isPluginEnabled(plugin))) {
                    log.debug("Enabling and starting plugin {}", plugin.getName());
                    this.pluginManager.setPluginEnabled(plugin, true);
                    try {
                        this.pluginManager.startPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        log.error("Error starting plugin {}", plugin.getName(), e);
                    }
                } else if (this.mode == ToggleMode.DISABLE || (this.mode == ToggleMode.TOGGLE && this.pluginManager.isPluginEnabled(plugin))) {
                    log.debug("Disabling and stopping plugin {}", plugin.getName());
                    this.pluginManager.setPluginEnabled(plugin, false);
                    try {
                        this.pluginManager.stopPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        log.error("Error stopping plugin {}", plugin.getName(), e);
                    }
                }
            });
        });
    }

    @Getter
    @AllArgsConstructor
    public enum ToggleMode implements Displayable {
        ENABLE("Enable", "Enable the plugin"),
        DISABLE("Disable", "Disable the plugin"),
        TOGGLE("Toggle", "Toggle the plugin"),
        ;

        private final String name;
        private final String tooltip;
    }
}
