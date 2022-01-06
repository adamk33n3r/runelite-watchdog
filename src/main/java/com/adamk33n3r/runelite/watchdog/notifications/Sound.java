package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Sound implements INotification {
    public String path;

    @Override
    public void fire(WatchdogPlugin plugin) {
        log.info("Fire Sound");
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
            // TODO: add control for the volume
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            log.info("volume is at: "+volume.getValue());
            volume.setValue(-10);
            clip.loop(0);
            return true;
        }

        return false;
    }
}
