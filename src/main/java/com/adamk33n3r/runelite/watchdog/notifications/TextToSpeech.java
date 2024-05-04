package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.elevenlabs.ElevenLabs;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;

import static net.runelite.client.RuneLite.CACHE_DIR;

@Slf4j
@Getter @Setter
@NoArgsConstructor
@Accessors(chain = true)
public class TextToSpeech extends MessageNotification implements IAudioNotification {
    private int gain = 5;
    private int rate = 1;
    @SerializedName("voice")
    private Voice legacyVoice = Voice.GEORGE;
    private TTSSource source = TTSSource.LEGACY;
    private String elevenLabsVoiceId;
    private transient com.adamk33n3r.runelite.watchdog.elevenlabs.Voice elevenLabsVoice;

    @Inject
    public TextToSpeech(WatchdogConfig config) {
        super(config);
        this.gain = config.defaultTTSVolume();
        this.rate = config.defaultTTSRate();
        this.legacyVoice = config.defaultTTSVoice();
        this.source = config.defaultTTSSource();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
            return;
        }

        String processedMessage = Util.processTriggerValues(this.message, triggerValues);

        try {
            String encodedMessage = URLEncoder.encode(processedMessage, "UTF-8");
            File watchdogPath = new File(CACHE_DIR, "watchdog");
            //noinspection ResultOfMethodCallIgnored
            watchdogPath.mkdirs();

            if (this.source == TTSSource.ELEVEN_LABS) {
                File soundFile = new File(watchdogPath, String.format("el-%s-%s.mp3", encodedMessage, this.elevenLabsVoiceId));
                if (soundFile.exists()) {
                    log.debug("Using cached file");
                    WatchdogPlugin.getInstance().getSoundPlayer().play(soundFile, this.gain);
                    return;
                }
                log.debug("generating tts");
                ElevenLabs.generateTTS(WatchdogPlugin.getInstance().getHttpClient(), this.elevenLabsVoice, processedMessage, (file) -> {
                    try {
                        Files.move(file.toPath(), soundFile.toPath());
                        WatchdogPlugin.getInstance().getSoundPlayer().play(soundFile, this.gain);
                    } catch (IOException e) {
                        log.error("Could not move tmp file to cache, playing from tmp", e);
                        WatchdogPlugin.getInstance().getSoundPlayer().play(file, this.gain);
                    }
                });
                return;
            }

            File soundFile = new File(watchdogPath, String.format("%s-%d-%d.wav", encodedMessage, this.rate, this.legacyVoice.id));

            // If the cache file exists, load and play it. Else fetch it from the server and cache it.
            if (soundFile.exists()) {
                log.debug("Using cached file");
            } else {
                String request = String.format("https://ttsplugin.com?m=%s&r=%d&v=%d", encodedMessage, this.rate, this.legacyVoice.id);
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
            }
            WatchdogPlugin.getInstance().getSoundPlayer().play(soundFile, this.gain);
        } catch (Exception ex) {
            log.error("Exception occurred while playing text to speech", ex);
        }
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setSource(this.watchdogConfig.defaultTTSSource());
        this.setLegacyVoice(this.watchdogConfig.defaultTTSVoice());
        // This will cause the tts panel to set the default
        this.setElevenLabsVoiceId(null);
        this.setGain(this.watchdogConfig.defaultTTSVolume());
        this.setRate(this.watchdogConfig.defaultTTSRate());
    }
}
