package com.adamk33n3r.runelite.watchdog;

import lombok.Builder;
import lombok.Data;
import net.runelite.api.WorldView;
import org.apache.commons.text.WordUtils;

import java.util.Arrays;
import java.util.Set;

// Use https://explv.github.io to find region ids
public enum Region {
    ALCHEMICAL_HYDRA(RegionConfig
        .builder()
        .regionIDs(Set.of(5536))
        .onlyInInstance()
        .build()),
    VARDORVIS(RegionConfig
        .builder()
        .regionIDs(Set.of(4405))
        .build()),
    LEVIATHAN(RegionConfig
        .builder()
        .regionIDs(Set.of(8291))
        .build()),
    WHISPERER(RegionConfig
        .builder()
        .regionIDs(Set.of(10595))
        .build()),
    SUCELLUS(RegionConfig
        .builder()
        .regionIDs(Set.of(12132))
        .build()),
    VORKATH(RegionConfig
        .builder()
        .regionIDs(Set.of(9023))
        .build()),
    INFERNO(RegionConfig
        .builder()
        .regionIDs(Set.of(9043))
        .build()),
    FIGHT_CAVE(RegionConfig
        .builder()
        .regionIDs(Set.of(9551))
        .build()),
    COLOSSEUM(RegionConfig
        .builder()
        .regionIDs(Set.of(7216))
        .build()),
    KALPHITE_QUEEN(RegionConfig
        .builder()
        .regionIDs(Set.of(13972))
        .planes(Set.of(0))
        .build()),
    COX(RegionConfig
        .builder()
        .regionIDs(Set.of(
            13136, // End of floor
            13137, 13393, // Lobbies/Room transitions
            13138, 13394, // Vasa/Tekton/Vespula Lizardmen/Skeletal Mystics/Guardian
            13139, 13395, 13140, 13396, // Puzzle rooms/bosses
            13141, 13397, // Rest room
            13145, // New floor
            13401, // New floor?
            12889 // Olm
        ))
        .build()),
    TOB(RegionConfig
        .builder()
        .regionIDs(Set.of(
            // 12869, // Lobby
            12613, // Maiden
            13125, // Bloat
            13122, // Nylocas
            13123, 13379, // Sotetseg/maze
            12612, // Xarpus
            12611 // Verzik
            // 12867 // Loot room

//            // Outside
//            14386,
//            14642
        ))
        .build()),
    TOA(RegionConfig
        .builder()
        .regionIDs(Set.of(
            // 13454, // Lobby
            14160, // Nexus Lobby
            15698, // Crondis
            15700, // Zebak
            14162, // Scabaras
            14164, // Kephri
            15186, // Apmeken
            15188, // Ba-Ba
            14674, // Het
            14676, // Akkha
            15184, 15696 // Wardens
            // 14672 // Chest room
        ))
        .build()),
    YAMA(RegionConfig
        .builder()
        .regionIDs(Set.of(6045))
        .build()),
    DOOM_OF_MOKHAIOTL(RegionConfig
        .builder()
        .regionIDs(Set.of(5269, 13668, 14180))
        .build()),
    NIGHTMARE(RegionConfig
        .builder()
        .regionIDs(Set.of(15515))
        .build()),
//    LUMBRIDGE_CASTLE(RegionConfig
//        .builder()
//        .regionIDs(Set.of(12850))
//        .planes(Set.of(0, 2))
//        .build()),
    ;

    final RegionConfig config;

    Region(RegionConfig config) {
        this.config = config;
    }

    public static boolean isBannedRegion(int regionID, WorldView worldView) {
        return Arrays.stream(values())
            .filter(r -> worldView.isInstance() || !r.config.onlyInInstance)
            .filter(r -> r.config.planes.isEmpty() || r.config.planes.contains(worldView.getPlane()))
            .flatMap(r -> r.config.regionIDs.stream())
            .anyMatch(id -> id == regionID);
    }

    public String toString() {
        return WordUtils.capitalizeFully(this.name().replaceAll("_", " "));
    }

    @Data
    @Builder
    static class RegionConfig {
        @Builder.Default
        public final Set<Integer> regionIDs = Set.of();
        @Builder.Default
        public final Set<Integer> planes = Set.of();
        public final boolean onlyInInstance;

        public static class RegionConfigBuilder {
            public RegionConfigBuilder onlyInInstance() {
                this.onlyInInstance = true;
                return this;
            }
        }
    }
}
