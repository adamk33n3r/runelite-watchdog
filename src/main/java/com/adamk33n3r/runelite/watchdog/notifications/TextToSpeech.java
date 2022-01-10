package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Slf4j
public class TextToSpeech extends NotificationWithMessage {
    public TextToSpeech() {
        this.message = "Hey! Wake up!";
    }

    @Override
    public void fire(WatchdogPlugin plugin) {
        try {
            File watchdogPath = new File(RuneLite.CACHE_DIR, "watchdog");
            //noinspection ResultOfMethodCallIgnored
            watchdogPath.mkdirs();
            String encodedMessage = URLEncoder.encode(this.message, "UTF-8");
            int rate = 1;
            int voice = 4;
            File soundFile = new File(watchdogPath, String.format("%s-%s-%s.wav", encodedMessage, rate, voice));

            AudioInputStream inputStream;

            // If the cache file exists, load and play it. Else fetch it from the server and cache it.
            if (soundFile.exists()) {
                inputStream = AudioSystem.getAudioInputStream(soundFile);
            } else {
                String request = String.format("https://ttsplugin.com?m=%s&r=%s&v=%s", encodedMessage, rate, voice);
                URLConnection conn = new URL(request).openConnection();
                byte[] bytes = new byte[conn.getContentLength()];
                InputStream stream = conn.getInputStream();
                for (int i = 0; i < conn.getContentLength(); i++) {
                    bytes[i] = (byte) stream.read();
                }
                // Write bytes to file in cache
                new FileOutputStream(soundFile).write(bytes);
                inputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
            }
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
