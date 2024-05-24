package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Sound extends AudioNotification {
    private String path;
    private int repeatDuration = 0;

    @Inject
    public Sound(WatchdogConfig config) {
        super(config);
        this.gain = config.defaultSoundVolume();
        this.path = config.defaultSoundPath();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        String processedPath = Util.processTriggerValues(path, triggerValues);
        WatchdogPlugin.getInstance().getSoundPlayer().play(new File(processedPath), gain, repeatDuration);
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setGain(this.watchdogConfig.defaultSoundVolume());
    }
}
