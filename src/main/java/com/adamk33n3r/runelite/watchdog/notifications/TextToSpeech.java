package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static net.runelite.client.RuneLite.CACHE_DIR;

@Slf4j
public class TextToSpeech extends MessageNotification implements IAudioNotification {
    @Getter @Setter
    private int gain = -10;
    @Getter @Setter
    private int rate = 1;
    @Getter @Setter
    private Voice voice = Voice.GEORGE;

    @Override
    protected void fireImpl(String[] triggerValues) {
        if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
            return;
        }

        try {
            String encodedMessage = URLEncoder.encode(Util.processTriggerValues(this.message, triggerValues), "UTF-8");
            File watchdogPath = new File(CACHE_DIR, "watchdog");
            //noinspection ResultOfMethodCallIgnored
            watchdogPath.mkdirs();
            File soundFile = new File(watchdogPath, String.format("%s-%d-%d.wav", encodedMessage, rate, voice.id));

            AudioInputStream inputStream;

            // If the cache file exists, load and play it. Else fetch it from the server and cache it.
            if (soundFile.exists()) {
                inputStream = AudioSystem.getAudioInputStream(soundFile);
            } else {
                String request = String.format("https://ttsplugin.com?m=%s&r=%d&v=%d", encodedMessage, rate, voice.id);
                URLConnection conn = new URL(request).openConnection();
                byte[] bytes = new byte[conn.getContentLength()];
                try (InputStream stream = conn.getInputStream()) {
                    for (int i = 0; i < conn.getContentLength(); i++) {
                        bytes[i] = (byte) stream.read();
                    }
                }
                // Write bytes to file in cache
                try (FileOutputStream fileOutputStream = new FileOutputStream(soundFile)) {
                    fileOutputStream.write(bytes);
                }
                inputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
            }
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            inputStream.close();
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(this.gain);
            log.debug("volume: " + this.gain);
            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
