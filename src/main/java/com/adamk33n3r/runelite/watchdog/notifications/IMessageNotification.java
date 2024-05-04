package com.adamk33n3r.runelite.watchdog.notifications;

public interface IMessageNotification extends INotification {
    String getMessage();
    IMessageNotification setMessage(String message);
}
