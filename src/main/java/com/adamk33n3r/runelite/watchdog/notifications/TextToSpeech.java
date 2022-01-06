package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.tts.MessageSegment;
import com.adamk33n3r.tts.TTSSegmentProcessor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class TextToSpeech extends NotificationWithMessage {
    @Inject
    private transient TTSSegmentProcessor ttsSegmentProcessor;

    public TextToSpeech() {
        this.message = "Hey! Wake up!";
    }

    @Override
    public void fire(WatchdogPlugin plugin) {
        log.info("Fire TextToSpeech");
        ttsSegmentProcessor.add(new MessageSegment(this.message));
    }
}
