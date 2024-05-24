package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Player;
import net.runelite.client.util.ColorUtil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import java.awt.Color;
import java.util.concurrent.ScheduledExecutorService;

@NoArgsConstructor
@Accessors(chain = true)
public class Overhead extends MessageNotification {
    @Getter @Setter
    private int displayTime = 3;
    @Getter @Setter
    private Color textColor = null;

    @Inject
    private transient Client client;
    @Inject
    private transient ScheduledExecutorService executor;

    @Inject
    public Overhead(WatchdogConfig config) {
        super(config);
        this.displayTime = config.defaultOverHeadDisplayTime();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        String message = Util.processTriggerValues(this.message, triggerValues);
        Player localPlayer = this.client.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (this.textColor != null) {
            sb.append("<col=")
                .append(ColorUtil.colorToHexCode(this.textColor))
                .append(">")
                .append(message)
                .append("</col>");
        } else {
            sb.append(message);
        }
        localPlayer.setOverheadText(sb.toString());
        localPlayer.setOverheadCycle(this.displayTime * 1000 / Constants.CLIENT_TICK_LENGTH);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setDisplayTime(this.watchdogConfig.defaultOverHeadDisplayTime());
    }
}
