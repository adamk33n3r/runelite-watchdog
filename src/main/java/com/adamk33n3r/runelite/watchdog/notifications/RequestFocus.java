package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import net.runelite.client.ui.ClientUI;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class RequestFocus extends Notification {
    private boolean forceFocus = false;

    @Inject
    private transient ClientUI clientUI;

    @Inject
    public RequestFocus(WatchdogConfig config) {
        super(config);
        this.forceFocus = config.defaultRequestFocusForce();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        if (this.forceFocus) {
            this.clientUI.forceFocus();
        } else {
            this.clientUI.requestFocus();
        }
    }
}
