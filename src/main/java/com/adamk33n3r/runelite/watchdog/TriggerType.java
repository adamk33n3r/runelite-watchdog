package com.adamk33n3r.runelite.watchdog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TriggerType {
    CHAT(),
    IDLE(),
    RESOURCE(),
    NOTIFICATION_FIRED(),
    STAT_DRAIN(),
;
}
