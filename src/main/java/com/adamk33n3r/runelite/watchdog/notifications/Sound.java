package com.adamk33n3r.runelite.watchdog.notifications;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Sound extends AudioNotification {
    public String path;

    @Override
    protected void fireImpl() {
        log.debug("trying to play: " + this.path);
        CompletableFuture.supplyAsync(this::tryPlaySound).thenAccept(playedCustom -> {
            log.debug("played custom: " + playedCustom);
            if (!playedCustom) {
                log.debug("playing default");
                Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    private boolean tryPlaySound() {
        try {
            File soundFile = new File(this.path);
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
