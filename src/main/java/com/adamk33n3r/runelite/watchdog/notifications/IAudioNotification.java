package com.adamk33n3r.runelite.watchdog.notifications;

public interface IAudioNotification extends INotification {
    int getGain();
    void setGain(int gain);
}
