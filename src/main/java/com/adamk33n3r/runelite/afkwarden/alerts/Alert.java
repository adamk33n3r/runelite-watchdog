package com.adamk33n3r.runelite.afkwarden.alerts;

import com.adamk33n3r.runelite.afkwarden.notifications.INotification;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Alert {
    private final String name;

    private final List<INotification> notifications = new ArrayList<>();

    public Alert(String name) {
        this.name = name;
    }
}
