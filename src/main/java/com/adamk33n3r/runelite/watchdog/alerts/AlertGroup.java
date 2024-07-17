package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class AlertGroup extends Alert {
    private List<Alert> alerts = new ArrayList<>();

    public AlertGroup() {
        this("New Alert Group");
    }

    public AlertGroup(String name) {
        super(name);
        // So that we don't serialize the empty array
        this.setNotifications(null);
    }
}
