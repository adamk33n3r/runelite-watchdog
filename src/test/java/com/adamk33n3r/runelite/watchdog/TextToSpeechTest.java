package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URLEncoder;
import java.util.UUID;

import static net.runelite.client.RuneLite.CACHE_DIR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextToSpeechTest {
    private static final File WATCHDOG_CACHE_DIR = new File(CACHE_DIR, "watchdog");
    private File createdFile;

    @Before
    public void setup() {
        WATCHDOG_CACHE_DIR.mkdirs();
    }

    @After
    public void teardown() {
        if (this.createdFile != null && this.createdFile.exists()) {
            this.createdFile.delete();
        }
    }

    @Test
    public void clearCache_returnsFalse_whenMessageIsEmpty() {
        TextToSpeech tts = new TextToSpeech();
        tts.setSource(TTSSource.ELEVEN_LABS)
            .setElevenLabsVoiceId("voice-id");
        assertFalse(tts.clearCache());
    }

    @Test
    public void clearCache_returnsFalse_whenFileDoesNotExist() {
        TextToSpeech tts = new TextToSpeech();
        tts.setSource(TTSSource.ELEVEN_LABS)
            .setElevenLabsVoiceId("voice-id")
            .setMessage("watchdog-test-" + UUID.randomUUID());
        assertFalse(tts.clearCache());
    }

    @Test
    public void clearCache_deletesElevenLabsFile_whenFileExists() throws Exception {
        String message = "watchdog-test-" + UUID.randomUUID();
        String voiceId = "test-voice-id";
        String encodedMessage = URLEncoder.encode(message, "UTF-8");
        this.createdFile = new File(WATCHDOG_CACHE_DIR, String.format("el-%s-%s.mp3", encodedMessage, voiceId));
        assertTrue(this.createdFile.createNewFile());

        TextToSpeech tts = new TextToSpeech();
        tts.setSource(TTSSource.ELEVEN_LABS)
            .setElevenLabsVoiceId(voiceId)
            .setMessage(message);
        assertTrue(tts.clearCache());
        assertFalse(this.createdFile.exists());
    }

    @Test
    public void clearCache_deletesLegacyFile_whenFileExists() throws Exception {
        String message = "watchdog-test-" + UUID.randomUUID();
        String encodedMessage = URLEncoder.encode(message, "UTF-8");
        int rate = 1;
        Voice voice = Voice.GEORGE;
        this.createdFile = new File(WATCHDOG_CACHE_DIR, String.format("%s-%d-%d.wav", encodedMessage, rate, voice.id));
        assertTrue(this.createdFile.createNewFile());

        TextToSpeech tts = new TextToSpeech();
        tts.setSource(TTSSource.LEGACY)
            .setRate(rate)
            .setLegacyVoice(voice)
            .setMessage(message);
        assertTrue(tts.clearCache());
        assertFalse(this.createdFile.exists());
    }
}
