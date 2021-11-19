package com.adamk33n3r.tts;

import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class TTSSegmentProcessor {
    @Inject
    @Getter
    private TTSSynth synth;

    @Getter
    private TTSSegment lastSegment;
    @Setter
    private boolean shouldSetLastSegment = true;
    private final Queue<TTSSegment> queue = new LinkedList<>();
    private boolean waiting = false;

    public void add(TTSSegment segment) {
        this.queue.add(segment);
        this.process();
    }

    public void clear() {
        this.synth.stop();
        this.queue.clear();
    }

    public void clearLastSegment() {
        this.lastSegment = null;
        this.shouldSetLastSegment = false;
    }

    public void process() {
        if (this.queue.isEmpty() || this.waiting)
            return;

        TTSSegment next = this.queue.peek();
        if (next == null)
            return;
        this.waiting = true;
        new Thread(() -> {
            boolean complete = next.process(this);
            if (complete)
                this.queue.remove(next);
            this.waiting = false;
            if (this.shouldSetLastSegment)
                this.lastSegment = next;
            else
                this.shouldSetLastSegment = true;
        }).start();
    }
}
