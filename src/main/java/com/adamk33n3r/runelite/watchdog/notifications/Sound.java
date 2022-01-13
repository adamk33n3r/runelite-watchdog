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
        CompletableFuture.supplyAsync(this::tryPlaySound).thenAccept(playedCustom -> {
            if (!playedCustom) {
                Toolkit.getDefaultToolkit().beep();
            }
        });
    }

    private boolean tryPlaySound() {
        File soundFile = new File(this.path);
        if (soundFile.exists()) {
            Clip clip;
            try {
                clip = AudioSystem.getClip();
            } catch (LineUnavailableException e) {
                return false;
            }

            try (InputStream fileStream = new BufferedInputStream(new FileInputStream(soundFile));
                 AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
                clip.open(sound);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                log.warn("Unable to load sound", e);
                return false;
            }
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(this.gain);
            log.info("volume: " + this.gain);
            clip.loop(0);
            return true;
        }

        return false;
    }
}
