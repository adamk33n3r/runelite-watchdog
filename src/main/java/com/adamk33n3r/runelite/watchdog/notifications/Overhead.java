package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import net.runelite.api.Client;
import net.runelite.api.Player;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Overhead extends MessageNotification {
    @Getter @Setter
    private int displayTime;

    @Inject
    private transient Client client;
    @Inject
    private transient ScheduledExecutorService executor;

    @Inject
    public Overhead(WatchdogConfig config) {
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
        this.executor.schedule(() -> {
            if (localPlayer.getOverheadText().equals(message)) {
                localPlayer.setOverheadText("");
            }
        }, this.displayTime, TimeUnit.SECONDS);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setDisplayTime(this.watchdogConfig.defaultOverHeadDisplayTime());
    }
}
