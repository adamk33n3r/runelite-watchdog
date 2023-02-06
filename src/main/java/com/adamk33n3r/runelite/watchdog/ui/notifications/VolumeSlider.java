package com.adamk33n3r.runelite.watchdog.ui.notifications;

import com.adamk33n3r.runelite.watchdog.notifications.IAudioNotification;

import javax.swing.JSlider;

public class VolumeSlider extends JSlider {

    public VolumeSlider(IAudioNotification audioNotification) {
        super(0, 10, audioNotification.getGain());
        this.setSnapToTicks(true);
        this.addChangeListener(ev -> audioNotification.setGain(this.getValue()));
    }
}
