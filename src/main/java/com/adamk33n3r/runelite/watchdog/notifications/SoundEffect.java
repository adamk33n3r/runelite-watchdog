package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import net.runelite.api.Client;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.callback.ClientThread;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;

@Getter
@Setter
@Accessors(chain = true)
public class SoundEffect extends AudioNotification {
    private int soundID;

    @Inject
    private transient Client client;

    @Inject
    private transient ClientThread clientThread;

    public SoundEffect() {
        this.gain = 8;
    }

    @Inject
    public SoundEffect(WatchdogConfig config) {
        super(config);
        this.gain = config.defaultSoundEffectVolume();
        this.soundID = config.defaultSoundEffectID();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.clientThread.invokeLater(() -> {
            this.client.playSoundEffect(this.soundID, Util.scale(this.gain, 0, 10, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH));
        });
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setSoundID(this.watchdogConfig.defaultSoundEffectID());
        this.setGain(this.watchdogConfig.defaultSoundEffectVolume());
    }
}
