package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.VariableNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PluginVar extends VariableNode {
    private final VarOutput<Boolean> value = new VarOutput<>(this, "Value", Boolean.class, false);
    private String pluginName;

    public PluginVar() {
        reg(this.value);
    }

    public void setValue(boolean value) {
        this.value.setValue(value);
    }
}
