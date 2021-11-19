package com.adamk33n3r.tts;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

public class DialogSegment extends MessageSegment {
    private String speakerName;
    private boolean saySpeakerName;
    public DialogSegment(String message, String speakerName, boolean saySpeakerName) {
        super(message);
        this.speakerName = speakerName;
        this.saySpeakerName = saySpeakerName;
    }

    @Override
    public boolean process(TTSSegmentProcessor processor) {
        StringBuilder sb = new StringBuilder();
        TTSSegment previousSegment = processor.getLastSegment();
        if (this.saySpeakerName) {
            if (previousSegment instanceof DialogSegment) {
                String prevSpeakerName = ((DialogSegment) previousSegment).speakerName;
                if (!this.speakerName.equals(prevSpeakerName))
                    sb.append(speakerName);
            } else {
                sb.append(speakerName);
            }
            sb.append(". ");
        }
        sb.append(this.message);
        return processor.getSynth().say(sb.toString());
    }
}
