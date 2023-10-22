package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;

@Slf4j
@Getter
@Setter
public class Sound extends AudioNotification {
    private String path;

    @Inject
    public Sound(WatchdogConfig config) {
        this.gain = config.defaultSoundVolume();
        this.path = config.defaultSoundPath();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        String processedPath = Util.processTriggerValues(this.path, triggerValues);
        WatchdogPlugin.getInstance().getSoundPlayer().play(new File(processedPath), this.gain);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setGain(this.watchdogConfig.defaultSoundVolume());
    }
}
