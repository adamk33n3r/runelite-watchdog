package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.PluginState;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointIn;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.PluginPickerInput;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

import java.awt.*;

@Getter
public class PluginNodePanel extends VariableNodePanel {
    private final ConnectionPointOut<Boolean> valueOut;

    public PluginNodePanel(GraphPanel graphPanel, PluginState node, int x, int y, String name, Color color, PluginManager pluginManager) {
        super(graphPanel, node, x, y, name, color);

        // Plugin picker — pluginName VarInput<String> stores the plugin display name
        ConnectionPointIn<String> pluginIn = new ConnectionPointIn<>(this, node.getPluginName());
        this.items.add(new ConnectionLine<>(pluginIn, new PluginPickerInput("Plugin", node.getPluginName(), pluginManager), null));

        // Side-effect: update the enabled-state boolean whenever the selected plugin changes
        addDisposer(node.getPluginName().onChange(pluginName -> {
            Plugin p = pluginName == null ? null : pluginManager.getPlugins().stream()
                .filter(pl -> pl.getName().equals(pluginName))
                .findFirst().orElse(null);
            node.setValue(p != null && pluginManager.isPluginEnabled(p));
            this.notifyChange();
        }));
        // Prime initial enabled state
        String initialName = node.getPluginName().getValue();
        Plugin initialPlugin = initialName == null ? null : pluginManager.getPlugins().stream()
            .filter(p -> p.getName().equals(initialName))
            .findFirst().orElse(null);
        node.setValue(initialPlugin != null && pluginManager.isPluginEnabled(initialPlugin));

        this.valueOut = new ConnectionPointOut<>(this, node.getValueOut());
        ViewInput<Boolean> valueView = new ViewInput<>("Active", node.getValue().getValue());
        addDisposer(node.getValue().onChange(a -> valueView.setValue(node.getValue().getValue())));
        this.items.add(new ConnectionLine<>(null, valueView, this.valueOut));

        this.pack();
    }
}
