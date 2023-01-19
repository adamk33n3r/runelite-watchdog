package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;

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
    private transient MP3Player mp3Player = new MP3Player();

    @Inject
    public Sound(WatchdogConfig config) {
        this.gain = config.defaultSoundVolume();
        this.path = config.defaultSoundPath();
    }

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
                if (soundFile.getName().endsWith(".mp3")) {
                    mp3Player.add(soundFile);
                    mp3Player.play();
                    mp3Player.getPlayList().clear();
                } else {
                    Clip clip = AudioSystem.getClip();
                    try (InputStream fileStream = new BufferedInputStream(new FileInputStream(soundFile));
                        AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
                        clip.open(sound);
                        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        int decibels = Util.scale(this.gain, 0, 10, -25, 5);
                        volume.setValue(decibels);
                        log.debug("volume: " + decibels);
                        clip.loop(0);
                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                clip.close();
                            }
                        });
                    } catch (Exception e) {
    //                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        log.warn("Unable to load sound", e);
                        clip.close();
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return false;
    }
}
