package com.adamk33n3r.nodegraph.nodes.constants;

import com.adamk33n3r.nodegraph.VarOutput;
import com.adamk33n3r.nodegraph.nodes.NoProcessNode;
import lombok.Getter;

@Getter
public class ConstantNode extends NoProcessNode {
    private final VarOutput<String> nameOut = new VarOutput<>(this, "Name", String.class, "");

    public ConstantNode() {
        reg(this.nameOut);
    }
}
