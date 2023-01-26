package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.FontType;
import net.runelite.client.ui.overlay.OverlayLayer;

@ConfigGroup("watchdog")
public interface WatchdogConfig extends Config {
    String CONFIG_GROUP_NAME = "watchdog";
    String ALERTS = "alerts";
    String ENABLE_TTS = "enableTTS";
    String OVERLAY_LAYER = "overlayLayer";
    String OVERLAY_FONT_TYPE = "overlayFontType";
    String OVERLAY_SHOW_TIME = "overlayShowTime";

    @ConfigItem(
        keyName = ALERTS,
        name = "Alerts",
        description = "Serialized Alerts as a JSON string",
        hidden = true
    )
    default String alerts() { return "[]"; }

    @ConfigSection(
        name = "Overlay",
        description = "The options that control the overlay notifications",
        position = 0
    )
    String overlaySection = "overlaySection";

    @ConfigItem(
        keyName = OVERLAY_LAYER,
        name = "Overlay Layer",
        description = "Which layer the overlay renders on. ABOVE_WIDGETS is default",
        section = overlaySection
    )
    default OverlayLayer overlayLayer() { return OverlayLayer.ABOVE_WIDGETS; }

    @ConfigItem(
        keyName = OVERLAY_FONT_TYPE,
        name = "Overlay Font Type",
        description = "Configures which font type is used for the overlay notifications",
        section = overlaySection
    )
    default FontType overlayFontType() { return FontType.BOLD; }

    @ConfigItem(
        keyName = OVERLAY_SHOW_TIME,
        name = "Overlay Show Time",
        description = "Shows how long ago the notification was fired on the overlay",
        section = overlaySection
    )
    default boolean overlayShowTime() { return true; }

    @ConfigItem(
        keyName = ENABLE_TTS,
        name = "Enable TTS",
        description = "Enables the TTS Notification Type",
        warning = "Using TTS will submit your IP address to a 3rd party website not controlled or verified by the RuneLite Developers."
    )
    default boolean ttsEnabled() { return false; }
}
