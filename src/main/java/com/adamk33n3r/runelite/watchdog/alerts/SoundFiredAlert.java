package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoundFiredAlert extends Alert {
    private int soundID = 2739;

    public SoundFiredAlert() {
        super("New Sound Fired Alert");
    }
}