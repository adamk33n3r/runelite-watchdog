package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.SpawnedAlert;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import java.util.function.Predicate;

@RequiredArgsConstructor
final class SpawnedEventData {
    final String unformattedName;
    final int id;
    final WorldPoint location;
    final SpawnedAlert.SpawnedDespawned despawned;
    final SpawnedAlert.SpawnedType type;
    final Predicate<SpawnedAlert> additionalFilter;
}
