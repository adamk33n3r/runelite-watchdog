package com.adamk33n3r.runelite.watchdog.alerts;

import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.ui.ComparableNumber;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SpawnedAlert extends Alert implements RegexMatcher {
    @Builder.Default
    private SpawnedDespawned spawnedDespawned = SpawnedDespawned.SPAWNED;
    @Builder.Default
    private SpawnedType spawnedType = SpawnedType.ITEM;
    @Builder.Default
    private String spawnedName = "";
    @Builder.Default
    private boolean regexEnabled = false;
    @Builder.Default
    private int distance = -1;
    @Builder.Default
    private ComparableNumber.Comparator distanceComparator = ComparableNumber.Comparator.LESS_THAN_OR_EQUALS;

    @Override
    public String getPattern() {
        return this.spawnedName;
    }

    @Override
    public void setPattern(String pattern) {
        this.spawnedName = pattern;
    }

    public SpawnedAlert() {
        super("New Spawned Alert");
    }

    public SpawnedAlert(String name) {
        super(name);
        // some drops
        // enemies spawn
        // trees/ore comes back
    }

    @Getter
    @AllArgsConstructor
    public enum SpawnedDespawned implements Displayable {
        SPAWNED("Spawned", "Object has spawned"),
        DESPAWNED("Despawned", "Object has despawned"),
        ;

        public final String name;
        public final String tooltip;
    }

    @Getter
    @AllArgsConstructor
    public enum SpawnedType implements Displayable {
        DECORATIVE_OBJECT("Decorative Object", "Decorative Object"),
        GAME_OBJECT("Game Object", "Something interactable, like a Tree"),
        GROUND_OBJECT("Ground Object", "Ground Object"),
        ITEM("Item", "Bones on the ground"),
        NPC("NPC", "An NPC, like the Wise Old Man"),
        PLAYER("Player", "Another player"),
        WALL_OBJECT("Wall Object", "Wall Object"),
        ;

        public final String name;
        public final String tooltip;
    }
}
