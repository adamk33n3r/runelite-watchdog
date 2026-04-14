package com.adamk33n3r.runelite.watchdog.ui.nodegraph.variables;

import com.adamk33n3r.nodegraph.nodes.constants.PluginVar;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.GraphPanel;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionLine;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.connections.ConnectionPointOut;
import com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs.ViewInput;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;

@Getter
public class PluginNodePanel extends VariableNodePanel {
    private final ConnectionPointOut<Boolean> valueOut;

    public PluginNodePanel(GraphPanel graphPanel, PluginVar node, int x, int y, String name, Color color, PluginManager pluginManager) {
        super(graphPanel, node, x, y, name, color, false);

        Plugin[] plugins = pluginManager.getPlugins()
            .stream().sorted(Comparator.comparing(Plugin::getName))
            .toArray(Plugin[]::new);
        Plugin selectedPlugin = node.getPluginName() == null ? null : pluginManager.getPlugins().stream()
            .filter(p -> p.getName().equals(node.getPluginName()))
            .findFirst()
            .orElse(null);
        // Set initial value
        node.setValue(pluginManager.isPluginEnabled(selectedPlugin));
        JComboBox<Plugin> pluginSelect = PanelUtils.createSelect(plugins, selectedPlugin, Plugin::getName, "Select a plugin...", selected -> {
            node.setPluginName(selected.getName());
            node.setValue(pluginManager.isPluginEnabled(selected));
            this.notifyChange();
        });
        this.items.add(pluginSelect);

        this.valueOut = new ConnectionPointOut<>(this, node.getValueOut());
        ViewInput<Boolean> valueView = new ViewInput<>("Active", node.getValue().getValue());
        addDisposer(node.getValue().onChange(a -> valueView.setValue(node.getValue().getValue())));
        this.items.add(new ConnectionLine<>(null, valueView, this.valueOut));

        this.pack();
    }
}
