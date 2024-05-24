package com.adamk33n3r.runelite.watchdog;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.ClientUI;

import jaco.mp3.player.MP3Player;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.*;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Singleton
public class SoundPlayer {
    @Inject
    private ClientUI clientUI;

    @Inject
    private Client client;

    @Inject
    private transient ClientThread clientThread;

    @Inject
    private WatchdogConfig config;

    private final MP3Player mp3Player;

    private final Queue<SoundItem> queue = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> soundPlayerFuture;
    private Timeout soundTimeout;

    private boolean mp3IsPlaying = false;
    private boolean clipIsPlaying = false;
    private long mouseLastPressedMillis;

    public SoundPlayer() {
        this.mp3Player = new MP3Player();
    }

    public void startUp() {
        this.soundPlayerFuture = this.executor.scheduleAtFixedRate(
                this::processQueue,
                0,
                100,
                TimeUnit.MILLISECONDS
        );
    }

    public void shutDown() {
        this.soundPlayerFuture.cancel(false);
        this.stop();
    }

    public void processQueue() {
        // Handle mp3 being over
        if (!this.clipIsPlaying && this.mp3IsPlaying && this.mp3Player.isStopped()) {
            if (this.mp3Player.isRepeat()) {
                this.mp3Player.play();
                return;
            } else {
                this.mp3IsPlaying = false;
            }
        }
        if (mp3IsPlaying || clipIsPlaying) {
            return;
        }

        this.playNext(this.mp3Player);
    }

    public void stop() {
        if (this.soundTimeout != null) {
            this.soundTimeout.stopAndRunNow();
            this.soundTimeout = null;
        }
        this.queue.clear();
    }

    public void play(File soundFile, int volume) {
        this.play(soundFile, volume, 0);
    }

    public void play(File soundFile, int volume, int repeatTime) {
        this.queue.add(new SoundItem(soundFile, volume, repeatTime));
        if (!WatchdogPlugin.getInstance().getConfig().putSoundsIntoQueue()) {
            SwingUtilities.invokeLater(() -> this.playNext(this.mp3Player));
        }
    }

    private void playNext(MP3Player mp3Player) {
        SoundItem nextSound = this.queue.poll();
        if (nextSound == null) {
            return;
        }

        if (!nextSound.getFile().exists()) {
            log.error(String.format("File not found: %s", nextSound.getFile().getAbsolutePath()));
            this.clipIsPlaying = true;
            Toolkit.getDefaultToolkit().beep();
            this.executor.schedule(() -> this.clipIsPlaying = false, 1, TimeUnit.SECONDS);
            return;
        }

        mouseLastPressedMillis = client.getMouseLastPressedMillis();

        log.debug(String.format("Now playing: %s", nextSound.getFile().getAbsolutePath()));

        if (nextSound.getFile().getName().endsWith(".mp3")) {
            mp3Player.getPlayList().clear();
            mp3Player.add(nextSound.getFile());
            mp3Player.setVolume(nextSound.getGain() * 10);
            this.mp3IsPlaying = true;
            if (this.soundTimeout != null) {
                this.soundTimeout.stopAndRunNow();
                this.soundTimeout = null;
            }
            mp3Player.play();
            // jaco.mp3 repeat functionality is broken, but we are using it to signal to ourselves to repeat on loop
            mp3Player.setRepeat(true);
            setTimeout(() -> {
                mp3Player.setRepeat(false);
            }, nextSound.getRepeatSeconds());
        } else {
            try {
                Clip clip = AudioSystem.getClip();
                try (InputStream fileStream = new BufferedInputStream(new FileInputStream(nextSound.getFile()));
                     AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
                    clip.open(sound);
                    FloatControl volumeFC = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    int decibels = Util.scale(nextSound.getGain(), 0, 10, -25, 5);
                    volumeFC.setValue(decibels);
                    this.clipIsPlaying = true;
                    clip.loop(0);
                    AtomicBoolean isLooping = new AtomicBoolean(true);
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            if (isLooping.get()) {
                                clip.setFramePosition(0);
                                clip.loop(0);
                            } else {
                                this.clipIsPlaying = false;
                                clip.close();
                            }
                        }
                    });

                    if (this.soundTimeout != null) {
                        this.soundTimeout.stopAndRunNow();
                        this.soundTimeout = null;
                    }

                    setTimeout(() -> {
                        isLooping.set(false);
                    }, nextSound.getRepeatSeconds());
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

    /**
     * Runs a method after a delay
     * <p>
     * Internally polls for user interaction if the delay is < 0
     */
    private void setTimeout(Runnable runnable, int delaySeconds) {
        if (delaySeconds == 0) {
            runnable.run();
            return;
        }

        if (delaySeconds < 0) {
            this.soundTimeout = new Interval(this.executor, (interval, stop) -> {
                if (this.hasUserInteraction() || stop) {
                    interval.stop();
                    runnable.run();
                }
            }, Constants.CLIENT_TICK_LENGTH, TimeUnit.MILLISECONDS);
            return;
        }
        this.soundTimeout = new Timeout(this.executor, (timeout, stop) -> runnable.run(), delaySeconds, TimeUnit.SECONDS);
    }

    private boolean hasUserInteraction() {
        // We poll this every client tick, if there was any activity in the past second, that counts
        int clientTicksSinceActivity = 1000 / Constants.CLIENT_TICK_LENGTH;
        if (((client.getMouseIdleTicks() < clientTicksSinceActivity && this.config.mouseMovementCancels())
                || client.getKeyboardIdleTicks() < clientTicksSinceActivity
                || client.getMouseLastPressedMillis() > mouseLastPressedMillis) && clientUI.isFocused()
        ) {
            return true;
        }
        return false;
    }

    private MP3Player createMP3Player() {
        MP3Player mp3Player = new MP3Player();
        mp3Player.setRepeat(false);
        return mp3Player;
    }
}
