package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@NoArgsConstructor
public class Overhead extends MessageNotification {
    @Getter @Setter
    private int displayTime = 3;

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
        localPlayer.setOverheadText(message);
        localPlayer.setOverheadCycle(this.displayTime * 1000 / Constants.CLIENT_TICK_LENGTH);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setDisplayTime(this.watchdogConfig.defaultOverHeadDisplayTime());
    }
}
