package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;

import lombok.extern.slf4j.Slf4j;

import java.awt.TrayIcon;

@Slf4j
public class TrayNotification extends MessageNotification {
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
