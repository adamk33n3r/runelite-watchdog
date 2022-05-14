package com.adamk33n3r.runelite.watchdog.notifications;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;

@Slf4j
public class TrayNotification extends MessageNotification {
    @Override
    protected void fireImpl() {
        log.debug("Fire TrayNotification");
        if (this.clientUI.getTrayIcon() != null) {
            this.clientUI.getTrayIcon().displayMessage("Watchdog", message, TrayIcon.MessageType.NONE);
        }
    }
}
