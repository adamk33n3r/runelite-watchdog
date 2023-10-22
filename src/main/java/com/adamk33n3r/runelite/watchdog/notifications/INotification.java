package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;

public interface INotification {
    Alert getAlert();
    void setAlert(Alert alert);
    void fire(String[] triggerValues);
    void setDefaults();
}
