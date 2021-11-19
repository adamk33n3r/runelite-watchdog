package com.adamk33n3r.runelite.afkwarden.notifications;

import com.adamk33n3r.runelite.afkwarden.AFKWardenPlugin;
import com.adamk33n3r.tts.MessageSegment;
import com.adamk33n3r.tts.TTSSegmentProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextToSpeech implements INotification {
    public String message;

    private TTSSegmentProcessor ttsSegmentProcessor;

    public TextToSpeech(TTSSegmentProcessor ttsSegmentProcessor) {
        this.ttsSegmentProcessor = ttsSegmentProcessor;
    }

    @Override
    public void fire(AFKWardenPlugin plugin) {
        log.info("Fire TextToSpeech");
        ttsSegmentProcessor.add(new MessageSegment(this.message));
    }
}
