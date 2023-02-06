package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;

import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.callback.ClientThread;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;

@Getter
@Setter
public class SoundEffect extends AudioNotification {
    private int soundID = SoundEffectID.GE_ADD_OFFER_DINGALING;

    @Inject
    private transient Client client;

    @Inject
    private transient ClientThread clientThread;

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.clientThread.invokeLater(() -> {
            this.client.playSoundEffect(this.soundID, Util.scale(this.gain, 0, 10, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH));
        });
    }
}
