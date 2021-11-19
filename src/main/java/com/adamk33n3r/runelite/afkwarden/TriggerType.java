package com.adamk33n3r.runelite.afkwarden;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TriggerType {
    CHAT(),
    IDLE(),
    RESOURCE(),
;
}
