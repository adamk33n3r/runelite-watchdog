package com.adamk33n3r.tts;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DelaySegment implements TTSSegment {
    public final int delay;
    private int delayRemaining = 0;

    public boolean process(TTSSegmentProcessor processor) {
        this.delayRemaining++;
        return this.delayRemaining > this.delay;
    }
}
