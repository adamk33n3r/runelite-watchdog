package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.TrayIcon;

@Slf4j
@NoArgsConstructor
public class TrayNotification extends MessageNotification {
    @Inject
    public TrayNotification(WatchdogConfig config) {
        super(config);
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        if (this.clientUI.getTrayIcon() != null) {
            this.clientUI.getTrayIcon().displayMessage(
                "Watchdog",
                Util.processTriggerValues(this.message, triggerValues),
                TrayIcon.MessageType.NONE);
        }
    }
}
