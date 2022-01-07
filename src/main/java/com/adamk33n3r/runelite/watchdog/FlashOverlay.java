package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;

import static net.runelite.api.widgets.WidgetID.*;

@Slf4j
public class FlashOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private ClientUI clientUI;

    private Instant flashStart;
    private long mouseLastPressedMillis;

    private ScreenFlash screenFlash;

    public FlashOverlay() {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    public void flash(ScreenFlash screenFlash) {
        this.screenFlash = screenFlash;
        this.flashStart = Instant.now();
        this.mouseLastPressedMillis = client.getMouseLastPressedMillis();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Copied from Notifier
        if (this.flashStart == null) {
            return null;
        }
        if (Instant.now().minusMillis(2000).isAfter(this.flashStart)) {
            switch (this.screenFlash.flashNotification)
            {
                case FLASH_TWO_SECONDS:
                case SOLID_TWO_SECONDS:
                    flashStart = null;
                    return null;
                case SOLID_UNTIL_CANCELLED:
                case FLASH_UNTIL_CANCELLED:
                    // Any interaction with the client since the notification started will cancel it after the minimum duration
                    if ((client.getMouseIdleTicks() < 2000 / Constants.CLIENT_TICK_LENGTH
                        || client.getKeyboardIdleTicks() < 2000 / Constants.CLIENT_TICK_LENGTH
                        || client.getMouseLastPressedMillis() > mouseLastPressedMillis) && clientUI.isFocused())
                    {
                        flashStart = null;
                        return null;
                    }
                    break;
            }
        }
        // Me: This can be weird depending on which game cycle the flash is fired
        if (client.getGameCycle() % 40 >= 20
            // For solid colour, fall through every time.
            && (this.screenFlash.flashNotification == FlashNotification.FLASH_TWO_SECONDS
            || this.screenFlash.flashNotification == FlashNotification.FLASH_UNTIL_CANCELLED))
        {
            return null;
        }
        graphics.setColor(this.screenFlash.color);
        graphics.fill(new Rectangle(this.client.getCanvas().getSize()));
        return null;
    }
}
