package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class SoundEffect extends AudioNotification {
    private int soundID;

    @Inject
    private transient Client client;

    @Inject
    private transient ClientThread clientThread;

    @Inject
    public SoundEffect(WatchdogConfig config) {
        super(config);
        this.setDefaults();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.clientThread.invokeLater(() -> {
            var userVolume = this.client.getPreferences().getSoundEffectVolume();
            this.client.getPreferences().setSoundEffectVolume(Util.scale(this.gain, 0, 10, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH));
            this.client.playSoundEffect(this.soundID, 0);
            this.client.getPreferences().setSoundEffectVolume(userVolume);
        });
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setSoundID(this.watchdogConfig.defaultSoundEffectID());
        this.setGain(this.watchdogConfig.defaultSoundEffectVolume());
    }
}
