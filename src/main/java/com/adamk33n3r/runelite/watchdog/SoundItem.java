package com.adamk33n3r.runelite.watchdog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@Getter
@RequiredArgsConstructor
public class SoundItem {
    private final File file;
    private final int gain;
    private final int repeatSeconds;
}
