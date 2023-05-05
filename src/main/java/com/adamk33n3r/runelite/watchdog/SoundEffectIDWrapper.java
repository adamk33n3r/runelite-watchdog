package com.adamk33n3r.runelite.watchdog;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SoundEffectIDWrapper {
    @Getter()
    private static final List<SoundEffect> soundEffects;

    public static final SoundEffect GE_TRADE_OK;

    static {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(SoundEffectIDWrapper.class.getResourceAsStream("sound_effects.tsv"))))) {
            soundEffects = br.lines()
                .map(line -> {
                    String[] split = line.split("\t");
                    String name = split.length > 1 ? split[1] : "NO_NAME";
                    return new SoundEffect(Integer.parseInt(split[0]), name);
                })
                .collect(Collectors.toList());
            GE_TRADE_OK = soundEffects.get(3925);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SoundEffect {
        private int id;
        private String name;

        @Override
        public String toString() {
            return this.name;
//            return this.id + " - " + this.name;
        }
    }
}
