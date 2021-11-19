package com.adamk33n3r.tts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageSegment implements TTSSegment {
    public final String message;

    public boolean process(TTSSegmentProcessor processor) {
        return processor.getSynth().say(this.message);
    }
}
