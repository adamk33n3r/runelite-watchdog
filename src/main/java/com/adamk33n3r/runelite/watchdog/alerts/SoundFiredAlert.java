package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SoundFiredAlert extends Alert {
    @Builder.Default
    private int soundID = 2739;

    public SoundFiredAlert() {
        super("New Sound Fired Alert");
    }
}