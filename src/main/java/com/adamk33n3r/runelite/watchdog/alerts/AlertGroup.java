package com.adamk33n3r.runelite.watchdog.alerts;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class AlertGroup extends Alert {
    @Builder.Default
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
