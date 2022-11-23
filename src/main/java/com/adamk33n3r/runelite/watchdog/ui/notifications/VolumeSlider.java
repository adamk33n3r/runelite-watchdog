package com.adamk33n3r.runelite.watchdog.ui.notifications;

import com.adamk33n3r.runelite.watchdog.notifications.IAudioNotification;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class VolumeSlider extends JSlider {

    public VolumeSlider(IAudioNotification audioNotification) {
        super(0, 10, (audioNotification.getGain() + 25) / 3);
//        this.setPaintTicks(true);
        this.setSnapToTicks(true);
        this.setMajorTickSpacing(5);
        this.setMinorTickSpacing(1);
        this.addChangeListener(ev -> {
            audioNotification.setGain(this.getValue() * 3 - 25);
        });
//        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }
}
