package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.VarOutput;
import lombok.Getter;

@Getter
public class PluginState extends VariableNode {
    private final VarInput<Boolean> value = new VarInput<>(this, "Value", Boolean.class, false);
    private final VarOutput<Boolean> valueOut = new VarOutput<>(this, "Value", Boolean.class, false);
    private final VarInput<String> pluginName = new VarInput<>(this, "Plugin", String.class, null);

    public PluginState() {
        this.value.onChange(val -> this.process());

        reg(this.value);
        reg(this.pluginName);
        reg(this.valueOut);
    }

    public void setValue(boolean value) {
        this.value.setValue(value);
    }

    @Override
    public void process() {
        this.valueOut.setValue(this.value.getValue());
    }
}
