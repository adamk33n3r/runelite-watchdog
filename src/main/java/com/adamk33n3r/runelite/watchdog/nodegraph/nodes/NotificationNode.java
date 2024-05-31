package com.adamk33n3r.runelite.watchdog.nodegraph.nodes;

import com.adamk33n3r.runelite.watchdog.nodegraph.Node;
import com.adamk33n3r.runelite.watchdog.nodegraph.VarInput;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
public class NotificationNode extends Node {
    @Setter
    private Notification notification;

    private final VarInput<Boolean> enabled = new VarInput<>(this, "Enabled", Boolean.class, true);
    private final VarInput<Boolean> fireWhenFocused = new VarInput<>(this, "Fire When Focused", Boolean.class, true);
    private final VarInput<Number> fireWhenAfk = new VarInput<>(this, "Fire When AFK", Number.class, 0);
    private final VarInput<Number> delayMilliseconds = new VarInput<>(this, "Delay (ms)", Number.class, 0);
    private final VarInput<String[]> captureGroups = new VarInput<>(this, "Capture Groups", String[].class, new String[0]);

    // Could maybe output "if fired" or something

    @Override
    public void process() {
        if (this.enabled.getValue()) {
            this.notification.fire(this.captureGroups.getValue());
        }
    }
}
