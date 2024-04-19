package com.adamk33n3r.runelite.watchdog;

import java.util.Arrays;

// Use https://explv.github.io to find region ids
public enum Region {
    ALCHEMICAL_HYDRA(5536),
    VARDORVIS(4405),
    LEVIATHAN(8291),
    WHISPERER(10595),
    SUCELLUS(12132),
    VORKATH(9023),
    INFERNO(9043),
    FIGHT_CAVE(9551),
    COLOSSEUM(7216),
    TEST(12850),

    COX(
        13137
    ),
    TOB(
//        12869, // Lobby
        12613, // Maiden
        13125, // Bloat
        13122, // Nylocas
        13123, 13379, // Sotetseg/maze
        12612, // Xarpus
        12611 // Verzik
//        12867 // Loot room
//        14386,
//        14642
    ),
    TOA(
//        13454, // Lobby
        14160, // Nexus Lobby
        15698, // Crondis
        15700, // Zebak
        14162, // Scabaras
        14164, // Kephri
        15186, // Apmeken
        15188, // Ba-Ba
        14674, // Het
        14676, // Akkha
        15184, 15696, // Wardens
        14672 // Tomb Lobby
    ),
    ;

    public final int[] regionIDs;

    Region(int... regionIDs) {
        this.regionIDs = regionIDs;
    }

    public static boolean isBannedRegion(int regionID) {
        return Arrays.stream(values())
            .flatMapToInt(r -> Arrays.stream(r.regionIDs))
            .anyMatch(id -> id == regionID);
    }
}
