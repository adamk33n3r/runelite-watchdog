package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Sound extends AudioNotification {
    public String path;

    @Override
    protected void fireImpl(String[] triggerValues) {
        String processedPath = Util.processTriggerValues(this.path, triggerValues);
        log.debug("trying to play: " + processedPath);
        CompletableFuture.supplyAsync(() -> tryPlaySound(processedPath)).thenAccept(playedCustom -> {
            log.debug("played custom: " + playedCustom);
            if (!playedCustom) {
                log.debug("playing default");
                Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    private boolean tryPlaySound(String path) {
        try {
            File soundFile = new File(path);
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();

                try (InputStream fileStream = new BufferedInputStream(new FileInputStream(soundFile));
                     AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
                    clip.open(sound);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    log.warn("Unable to load sound", e);
                    return false;
                }
                FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue(this.gain);
                log.debug("volume: " + this.gain);
                clip.loop(0);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return false;
    }
}
