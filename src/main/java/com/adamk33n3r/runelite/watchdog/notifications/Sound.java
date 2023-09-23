package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import jaco.mp3.player.MP3Player;

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
}
