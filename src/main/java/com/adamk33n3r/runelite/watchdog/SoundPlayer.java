package com.adamk33n3r.runelite.watchdog;

import jaco.mp3.player.MP3Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Singleton
public class SoundPlayer {
    @Inject
    private ScheduledExecutorService executor;

    private final MP3Player mp3Player;

    private final Queue<Pair<File, Integer>> queue = new ConcurrentLinkedQueue<>();

    private boolean mp3IsPlaying = false;
    private boolean clipIsPlaying = false;

    public SoundPlayer() {
        this.mp3Player = this.createMP3Player();
        // So that we don't get errors since the mp3 player creates buttons
//        SwingUtilities.invokeLater(() -> this.mp3Player = new MP3PlayerExt());
    }

    public void clearQueue() {
        this.queue.clear();
    }

    public void processQueue() {
        // Handle mp3 being over
        if (!this.clipIsPlaying && this.mp3IsPlaying && this.mp3Player.isStopped()) {
            this.mp3IsPlaying = false;
        }
        if (mp3IsPlaying || clipIsPlaying) {
            return;
        }

        this.playNext(this.mp3Player);
    }

    public void play(File soundFile, int volume) {
        this.queue.add(Pair.of(soundFile, volume));
        if (!WatchdogPlugin.getInstance().getConfig().putSoundsIntoQueue()) {
            this.playNext(this.createMP3Player());
        }
    }

    private void playNext(MP3Player mp3Player) {
        Pair<File, Integer> nextSound = this.queue.poll();
        if (nextSound == null) {
            return;
        }

        if (!nextSound.getLeft().exists()) {
            log.error(String.format("File not found: %s", nextSound.getLeft().getAbsolutePath()));
            this.clipIsPlaying = true;
            Toolkit.getDefaultToolkit().beep();
            this.executor.schedule(() -> this.clipIsPlaying = false, 1, TimeUnit.SECONDS);
            return;
        }

        log.debug(String.format("Now playing: %s", nextSound.getLeft().getAbsolutePath()));

        if (nextSound.getLeft().getName().endsWith(".mp3")) {
            mp3Player.getPlayList().clear();
            mp3Player.add(nextSound.getLeft());
            mp3Player.setVolume(nextSound.getRight() * 10);
            this.mp3IsPlaying = true;
            mp3Player.play();
        } else  {
            try {
                Clip clip = AudioSystem.getClip();
                try (InputStream fileStream = new BufferedInputStream(new FileInputStream(nextSound.getLeft()));
                     AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
                    clip.open(sound);
                    FloatControl volumeFC = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    int decibels = Util.scale(nextSound.getRight(), 0, 10, -25, 5);
                    volumeFC.setValue(decibels);
                    this.clipIsPlaying = true;
                    clip.loop(0);
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            this.clipIsPlaying = false;
                            clip.close();
                        }
                    });
                } catch (Exception e) {
//                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    log.warn("Unable to load sound", e);
                    clip.close();
                }
            } catch (Exception e) {
                log.error("Error trying to create clip", e);
            }
        }
    }

    private MP3Player createMP3Player() {
        MP3PlayerExt mp3Player = new MP3PlayerExt();
        mp3Player.setRepeat(false);
        return mp3Player;
    }
}
