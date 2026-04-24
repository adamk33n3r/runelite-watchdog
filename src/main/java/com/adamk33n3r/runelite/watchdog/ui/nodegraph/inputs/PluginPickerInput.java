package com.adamk33n3r.runelite.watchdog.ui.nodegraph.inputs;

import com.adamk33n3r.nodegraph.Var;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class PluginPickerInput extends AbstractInput<String> {
    private final JComboBox<Plugin> combo;
    private final PluginManager pluginManager;
    private boolean suppressEvents = false;
    private final List<Consumer<String>> listeners = new ArrayList<>();

    public PluginPickerInput(String label, Var<String> value, PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        JLabel labelComp = new JLabel(label);

        Plugin[] plugins = pluginManager.getPlugins()
            .stream().sorted(Comparator.comparing(Plugin::getName))
            .toArray(Plugin[]::new);
        Plugin selected = this.findPlugin(value.getValue());

        this.combo = PanelUtils.createSelect(plugins, selected, Plugin::getName, "Select a plugin...", p -> {
            if (!this.suppressEvents && p != null) {
                this.listeners.forEach(c -> c.accept(p.getName()));
            }
        });
        this.registerOnChange(value::setValue);
        this.add(labelComp, BorderLayout.WEST);
        this.add(this.combo);
    }

    private Plugin findPlugin(String name) {
        if (name == null) return null;
        return this.pluginManager.getPlugins().stream()
            .filter(p -> p.getName().equals(name))
            .findFirst().orElse(null);
    }

    @Override
    public String getValue() {
        Plugin p = (Plugin) this.combo.getSelectedItem();
        return p == null ? null : p.getName();
    }

    @Override
    public void setValue(String value) {
        this.suppressEvents = true;
        try {
            this.combo.setSelectedItem(this.findPlugin(value));
        } finally {
            this.suppressEvents = false;
        }
    }

    @Override
    public void registerOnChange(Consumer<String> onChange) {
        this.listeners.add(onChange);
    }

    @Override
    protected JComponent getValueComponent() {
        return this.combo;
    }
}
