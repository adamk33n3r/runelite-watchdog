package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.ui.overlay.OverlayLayer;

@ConfigGroup("watchdog")
public interface WatchdogConfig extends Config {
    String CONFIG_GROUP_NAME = "watchdog";
    String ALERTS = "alerts";
    String ENABLE_TTS = "enableTTS";
    String OVERLAY_LAYER = "overlayLayer";

    @ConfigItem(
        keyName = ALERTS,
        name = "Alerts",
        description = "Serialized Alerts as a JSON string",
        hidden = true
    )
    default String alerts() { return "[]"; }

    @ConfigItem(
        keyName = OVERLAY_LAYER,
        name = "Overlay Layer",
        description = "Which layer the overlay renders on. ABOVE_WIDGETS is default"
    )
    default OverlayLayer overlayLayer() { return OverlayLayer.ABOVE_WIDGETS; }

    @ConfigItem(
        keyName = ENABLE_TTS,
        name = "Enable TTS",
        description = "Enables the TTS Notification Type",
        warning = "Using TTS will submit your IP address to a 3rd party website not controlled or verified by the RuneLite Developers."
    )
    default boolean ttsEnabled() { return false; }
}
