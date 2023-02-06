package com.adamk33n3r.runelite.watchdog;

import net.runelite.api.SoundEffectVolume;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NumberScaleTest {
    @Test
    public void test_scaling_decibels() {
        assertEquals(-25, Util.scale(0, 0, 10, -25, 5));
        assertEquals(5, Util.scale(10, 0, 10, -25, 5));

        assertEquals(0, Util.scale(-25, -25, 5, 0, 10));
        assertEquals(10, Util.scale(5, -25, 5, 0, 10));
    }

    @Test
    public void test_sound_effect_volume() {
        assertEquals(SoundEffectVolume.MUTED, Util.scale(0, 0, 10, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH));
        assertEquals(SoundEffectVolume.HIGH, Util.scale(10, 0, 10, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH));

        assertEquals(0, Util.scale(SoundEffectVolume.MUTED, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH, 0, 10));
        assertEquals(10, Util.scale(SoundEffectVolume.HIGH, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH, 0, 10));
        assertEquals(5, Util.scale(SoundEffectVolume.MEDIUM_LOW, SoundEffectVolume.MUTED, SoundEffectVolume.HIGH, 0, 10));
    }
}
