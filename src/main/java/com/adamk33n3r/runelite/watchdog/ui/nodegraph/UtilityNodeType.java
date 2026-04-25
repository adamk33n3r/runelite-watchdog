package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.nodegraph.Node;
import com.adamk33n3r.nodegraph.nodes.utility.DisplayNode;
import com.adamk33n3r.nodegraph.nodes.utility.NoteNode;
import com.adamk33n3r.nodegraph.nodes.utility.ToStringNode;
import com.adamk33n3r.runelite.watchdog.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UtilityNodeType implements Displayable {
    NOTE("Note", "Add a sticky note to the graph", NoteNode.class, NoteNodePanel.class),
    DISPLAY("Display", "Display the value of any input", DisplayNode.class, DisplayNodePanel.class),
    TO_STRING("To String", "Converts any value to its string representation", ToStringNode.class, ToStringNodePanel.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Node> implClass;
    private final Class<? extends NodePanel> nodePanelClass;
}
