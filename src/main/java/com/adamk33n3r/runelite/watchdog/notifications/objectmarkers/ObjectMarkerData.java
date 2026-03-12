package com.adamk33n3r.runelite.watchdog.notifications.objectmarkers;

import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Value
class ObjectMarkerData {
    ObjectMarker marker;
    Instant timeStarted;
    boolean sticky;

    public ObjectMarkerData(ObjectMarker marker) {
        this(marker, marker.isSticky());
    }

    public ObjectMarkerData(ObjectMarker marker, boolean stickyOverride) {
        this.marker = Objects.requireNonNull(marker);
        this.timeStarted = Instant.now();
        this.sticky = stickyOverride;
    }

    public boolean isExpired() {
        if (this.sticky) {
            return false;
        }
        return this.timeStarted.plus(Duration.ofSeconds(this.marker.getDisplayTime())).isBefore(Instant.now());
    }
}
