package com.adamk33n3r.nodegraph.nodes.utility;

import com.adamk33n3r.nodegraph.VarInput;
import com.adamk33n3r.nodegraph.nodes.NoProcessNode;
import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class NoteNode extends NoProcessNode {
    private final VarInput<String> note = new VarInput<>(this, "Note", String.class, "");
    /** Set only for unknown-type placeholders — the original type string. */
    private transient String originalType;
    /** Raw JSON payload for unknown-type nodes; re-emitted on serialize to enable lossless resave. */
    private transient JsonObject originalJson;

    public NoteNode() {
        reg(this.note);
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }

    public void setOriginalJson(JsonObject originalJson) {
        this.originalJson = originalJson;
    }
}
