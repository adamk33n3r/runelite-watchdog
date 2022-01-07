package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.util.ColorUtil;

import java.awt.Color;

@Slf4j
public class ScreenFlash implements INotification {
    public Color color = ColorUtil.fromHex("#46FF0000");
    public FlashNotification flashNotification = FlashNotification.SOLID_TWO_SECONDS;

    @Override
    public void fire(WatchdogPlugin plugin) {
        plugin.getFlashOverlay().flash(this);
    }
}
