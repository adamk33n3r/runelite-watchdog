package com.adamk33n3r.nodegraph.nodes.utility;

import com.adamk33n3r.nodegraph.Node;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteNode extends Node {
    private String note = "";
    /** Set only for unknown-type placeholders — the original type string. */
    private transient String originalType;
    /** Raw JSON payload for unknown-type nodes; re-emitted on serialize to enable lossless resave. */
    private transient JsonObject originalJson;
}
