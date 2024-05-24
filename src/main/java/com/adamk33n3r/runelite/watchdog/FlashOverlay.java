package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.time.Instant;

@Slf4j
public class FlashOverlay extends Overlay {
    @Inject
    private Client client;

    @Inject
    private ClientUI clientUI;

    @Inject
    private WatchdogConfig config;

    private Instant flashStart;
    private int gameCycleStart;
    private long mouseLastPressedMillis;

    private ScreenFlash screenFlash;

    private static final int MIN_MILLISECONDS_BEFORE_CANCELLED = 2000;

    public FlashOverlay() {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    public void flash(ScreenFlash screenFlash) {
        this.screenFlash = screenFlash;
        this.flashStart = Instant.now();
        this.gameCycleStart = this.client.getGameCycle();
        this.mouseLastPressedMillis = client.getMouseLastPressedMillis();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Adapted from Notifier
        if (this.flashStart == null) {
            return null;
        }

        if (this.screenFlash.getFlashDuration() == 0) {
            // Any interaction with the client since the notification started will cancel it after the minimum duration
            if (Instant.now().minusMillis(MIN_MILLISECONDS_BEFORE_CANCELLED).isAfter(this.flashStart)
                && ((client.getMouseIdleTicks() < MIN_MILLISECONDS_BEFORE_CANCELLED / Constants.CLIENT_TICK_LENGTH && this.config.mouseMovementCancels())
                || client.getKeyboardIdleTicks() < MIN_MILLISECONDS_BEFORE_CANCELLED / Constants.CLIENT_TICK_LENGTH
                || client.getMouseLastPressedMillis() > mouseLastPressedMillis) && clientUI.isFocused()
            ) {
                flashStart = null;
                return null;
            }
        } else if (Instant.now().minusSeconds(this.screenFlash.getFlashDuration()).isAfter(this.flashStart)) {
            flashStart = null;
            return null;
        }

        // Me: This can be weird depending on which game cycle the flash is fired
        if ((this.client.getGameCycle() - this.gameCycleStart) % 40 >= 20
            // For solid colour, fall through every time.
            && this.screenFlash.getFlashMode() == FlashMode.FLASH)
        {
            return null;
        }
        Color color = this.screenFlash.getColor();
        if (this.screenFlash.getFlashMode() == FlashMode.SMOOTH_FLASH) {
            color = Util.colorAlpha(color, this.getAlpha(color.getAlpha()));
        }
        graphics.setColor(color);
        graphics.fill(new Rectangle(this.client.getCanvas().getSize()));
        return null;
    }

    private int getAlpha(int maxAlpha) {
        int scaledAlpha = Util.scale((this.client.getGameCycle() - this.gameCycleStart) % 40, 0, 40, 0, maxAlpha*2);
        return scaledAlpha <= maxAlpha ? scaledAlpha : maxAlpha*2 - scaledAlpha;
    }
}
