package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import net.runelite.client.util.ColorUtil;

import lombok.Getter;
import lombok.Setter;

import java.awt.Color;

@Getter
@Setter
public class Overlay extends MessageNotification {
    private Color color = ColorUtil.fromHex("#46FF0000");
    private boolean sticky = false;
    private int timeToLive = 5;

    @Override
    protected void fireImpl(String[] triggerValues) {
        WatchdogPlugin.getInstance().getNotificationOverlay().add(this);
    }
}
