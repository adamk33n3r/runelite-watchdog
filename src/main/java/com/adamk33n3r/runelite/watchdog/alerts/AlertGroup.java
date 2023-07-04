package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AlertGroup extends Alert {
    private List<Alert> alerts = new ArrayList<>();

    public AlertGroup() {
        super("New Alert Group");
        // So that we don't serialize the empty array
        this.setNotifications(null);
    }
}
