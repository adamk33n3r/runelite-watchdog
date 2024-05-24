package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;

import net.runelite.api.SoundEffectID;
import net.runelite.client.config.*;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.util.ColorUtil;

import java.awt.Color;

@ConfigGroup(WatchdogConfig.CONFIG_GROUP_NAME)
public interface WatchdogConfig extends Config {
    String CONFIG_GROUP_NAME = "watchdog";
    Color DEFAULT_NOTIFICATION_COLOR = ColorUtil.fromHex("#46FF0000");
    Color DEFAULT_NOTIFICATION_TEXT_COLOR = Color.WHITE;

    // Hidden
    String ALERTS = "alerts";
    String PLUGIN_VERSION = "pluginVersion";

    // Core
    String ENABLE_TTS = "enableTTS";
    String OVERRIDE_IMPORTS_WITH_DEFAULTS = "overrideImportsWithDefaults";

    // AFK Notification
    String DEFAULT_AFK_MODE = "defaultAFKMode";
    String DEFAULT_AFK_SECONDS = "defaultAFKSeconds";

    // Overhead
    String DEFAULT_OVERHEAD_DISPLAY_TIME = "defaultOverheadDisplayTime";

    // Overlay
    String OVERLAY_LAYER = "overlayLayer";
    String OVERLAY_FONT_TYPE = "overlayFontType";
    String OVERLAY_SHOW_TIME = "overlayShowTime";
    String DEFAULT_OVERLAY_STICKY = "defaultOverlaySticky";
    String DEFAULT_OVERLAY_TEXT_COLOR = "defaultOverlayTextColor";
    String DEFAULT_OVERLAY_COLOR = "defaultOverlayColor";
    String DEFAULT_OVERLAY_TTL = "defaultOverlayTTL";
    String DEFAULT_OVERLAY_IMAGE_PATH = "defaultOverlayImagePath";

    // Screen Flash
    String MOUSE_MOVEMENT_CANCELS_FLASH = "mouseMovementCancelsFlash";
    String DEFAULT_SCREEN_FLASH_COLOR = "defaultScreenFlashColor";
    String DEFAULT_SCREEN_FLASH_TYPE = "defaultScreenFlashType";
    String DEFAULT_SCREEN_FLASH_MODE = "defaultScreenFlashMode";
    String DEFAULT_SCREEN_FLASH_DURATION = "defaultScreenFlashDuration";

    // Sound
    String PUT_SOUNDS_INTO_QUEUE = "putSoundsIntoQueue";
    String DEFAULT_SOUND_VOLUME = "defaultSoundVolume";
    String DEFAULT_SOUND_PATH = "defaultSoundPath";

    // Sound Effect
    String DEFAULT_SOUND_EFFECT_ID = "defaultSoundEffectID";
    String DEFAULT_SOUND_EFFECT_VOLUME = "defaultSoundEffectVolume";

    // TTS
    String DEFAULT_TTS_VOLUME = "defaultTTSVolume";
    String DEFAULT_TTS_SOURCE = "defaultTTSSource";
    String DEFAULT_TTS_VOICE = "defaultTTSVoice";
    String DEFAULT_TTS_RATE = "defaultTTSRate";
    String ELEVEN_LABS_API_KEY = "elevenLabsAPIKey";
    String DEFAULT_ELEVEN_LABS_VOICE = "defaultElevenLabsVoice";

    // Request Focus
    String DEFAULT_FORCE_FOCUS = "defaultForceFocus";

    //region Hidden
    @ConfigItem(
        keyName = ALERTS,
        name = "Alerts",
        description = "Serialized Alerts as a JSON string",
        hidden = true
    )
    default String alerts() { return "[]"; }

    @ConfigItem(
        keyName = PLUGIN_VERSION,
        name = "Plugin Version",
        description = "Last version of the plugin loaded",
        hidden = true
    )
    default String pluginVersion() { return null; }
    //endregion

    @ConfigItem(
        keyName = ENABLE_TTS,
        name = "Enable TTS",
        description = "Enables the TTS Notification Type",
        warning = "Using TTS will submit your IP address to a 3rd party website not controlled or verified by the RuneLite Developers."
    )
    default boolean ttsEnabled() { return false; }

    @ConfigItem(
        keyName = OVERRIDE_IMPORTS_WITH_DEFAULTS,
        name = "Override Imports with Defaults",
        description = "Will override imported alerts with your defaults set here"
    )
    default boolean overrideImportsWithDefaults() { return false; }

    @ConfigItem(
        keyName = PUT_SOUNDS_INTO_QUEUE,
        name = "Put Sounds Into Queue",
        description = "When this is on, all sounds will be queued up so that they will not overlap"
    )
    default boolean putSoundsIntoQueue() { return true; }

    @ConfigItem(
        keyName = MOUSE_MOVEMENT_CANCELS_FLASH,
        name = "Mouse Movement Cancels",
        description = "Cancel the repeated sounds/flashes with mouse movement as well as click and keyboard"
    )
    default boolean mouseMovementCancels() { return true; }

    //region AFK Notification
    @ConfigSection(
        name = "AFK Notification",
        description = "The options that control the afk notification settings",
        position = 0
    )
    String afkNotificationSection = "afkNotificationSection";

    @ConfigItem(
        keyName = DEFAULT_AFK_MODE,
        name = "Default AFK Mode",
        description = "The default AFK mode on/off",
        section = afkNotificationSection
    )
    default boolean defaultAFKMode() { return false; }

    @ConfigItem(
        keyName = DEFAULT_AFK_SECONDS,
        name = "Default AFK Seconds",
        description = "The default AFK seconds value",
        section = afkNotificationSection
    )
    @Units(Units.SECONDS)
    @Range(min = 1)
    default int defaultAFKSeconds() { return 5; }
    //endregion

    //region Overhead
    @ConfigSection(
        name = "Overhead",
        description = "The options that control the overhead notifications",
        position = 1,
        closedByDefault = true
    )
    String overheadSection = "overheadSection";

    @ConfigItem(
        keyName = DEFAULT_OVERHEAD_DISPLAY_TIME,
        name = "Default Display Time",
        description = "The default display time",
        section = overheadSection
    )
    @Units(Units.SECONDS)
    default int defaultOverHeadDisplayTime() { return 3; };
    //endregion

    //region Overlay
    @ConfigSection(
        name = "Overlay",
        description = "The options that control the overlay notifications",
        position = 2,
        closedByDefault = true
    )
    String overlaySection = "overlaySection";

    @ConfigItem(
        keyName = OVERLAY_LAYER,
        name = "Overlay Layer",
        description = "Which layer the overlay renders on. ABOVE_WIDGETS is default",
        section = overlaySection,
        position = 0
    )
    default OverlayLayer overlayLayer() { return OverlayLayer.ABOVE_WIDGETS; }

    @ConfigItem(
        keyName = OVERLAY_FONT_TYPE,
        name = "Overlay Font Type",
        description = "Configures which font type is used for the overlay notifications",
        section = overlaySection,
        position = 1
    )
    default FontType overlayFontType() { return FontType.BOLD; }

    @ConfigItem(
        keyName = OVERLAY_SHOW_TIME,
        name = "Overlay Show Time",
        description = "Shows how long ago the notification was fired on the overlay",
        section = overlaySection,
        position = 2
    )
    default boolean overlayShowTime() { return true; }

    @ConfigItem(
        keyName = DEFAULT_OVERLAY_STICKY,
        name = "Default Sticky",
        description = "The default sticky",
        section = overlaySection,
        position = 3
    )
    default boolean defaultOverlaySticky() { return false; }

    @ConfigItem(
        keyName = DEFAULT_OVERLAY_TTL,
        name = "Default Display Time",
        description = "The default time to display",
        section = overlaySection,
        position = 4
    )
    @Units(Units.SECONDS)
    default int defaultOverlayTTL() { return 5; }

    @ConfigItem(
        keyName = DEFAULT_OVERLAY_TEXT_COLOR,
        name = "Default Text Color",
        description = "The default text color",
        section = overlaySection,
        position = 5
    )
    default Color defaultOverlayTextColor() { return DEFAULT_NOTIFICATION_TEXT_COLOR; }

    @ConfigItem(
        keyName = DEFAULT_OVERLAY_COLOR,
        name = "Default Background Color",
        description = "The default background color",
        section = overlaySection,
        position = 6
    )
    @Alpha
    default Color defaultOverlayColor() { return DEFAULT_NOTIFICATION_COLOR; }

    @ConfigItem(
        keyName = DEFAULT_OVERLAY_IMAGE_PATH,
        name = "Default Image Path",
        description = "The default image path",
        section = overlaySection,
        position = 7
    )
    default String defaultOverlayImagePath() { return ""; }
    //endregion

    //region Screen Flash
    @ConfigSection(
        name = "Screen Flash",
        description = "The options that control the screen flash notifications",
        position = 3,
        closedByDefault = true
    )
    String screenFlashSection = "screenFlashSection";

    @ConfigItem(
        keyName = DEFAULT_SCREEN_FLASH_COLOR,
        name = "Default Color",
        description = "The default color",
        section = screenFlashSection,
        position = 1
    )
    @Alpha
    default Color defaultScreenFlashColor() { return DEFAULT_NOTIFICATION_COLOR; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_FLASH_TYPE,
        name = "Default Flash Type",
        description = "The default flash type",
        section = screenFlashSection,
        position = 2
    )
    default FlashNotification defaultScreenFlashType() { return FlashNotification.SOLID_TWO_SECONDS; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_FLASH_MODE,
        name = "Default Flash Mode",
        description = "The default flash mode",
        section = screenFlashSection,
        position = 3
    )
    default FlashMode defaultScreenFlashMode() { return FlashMode.FLASH; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_FLASH_DURATION,
        name = "Default Flash Duration",
        description = "The default flash duration in seconds",
        section = screenFlashSection,
        position = 3
    )
    @Units(Units.SECONDS)
    default int defaultScreenFlashDuration() { return 2; }
    //endregion

    //region Sound
    @ConfigSection(
        name = "Custom Sound",
        description = "The options that control the custom sound notifications",
        position = 4,
        closedByDefault = true
    )
    String soundSection = "soundSection";

    @ConfigItem(
        keyName = DEFAULT_SOUND_VOLUME,
        name = "Default Volume",
        description = "The default volume",
        section = soundSection
    )
    @Range(min = 0, max = 10)
    default int defaultSoundVolume() { return 8; }

    @ConfigItem(
        keyName = DEFAULT_SOUND_PATH,
        name = "Default Path",
        description = "The default path",
        section = soundSection
    )
    default String defaultSoundPath() { return null; }
    //endregion

    //region Sound Effect
    @ConfigSection(
        name = "Sound Effect",
        description = "The options that control the custom sound notifications",
        position = 5,
        closedByDefault = true
    )
    String soundEffectSection = "soundEffectSection";

    @ConfigItem(
        keyName = DEFAULT_SOUND_EFFECT_ID,
        name = "Default Sound Effect",
        description = "The default sound effect ID",
        section = soundEffectSection
    )
    default int defaultSoundEffectID() { return SoundEffectID.GE_ADD_OFFER_DINGALING; }

    @ConfigItem(
        keyName = DEFAULT_SOUND_EFFECT_VOLUME,
        name = "Default Volume",
        description = "The default volume",
        section = soundEffectSection
    )
    @Range(min = 0, max = 10)
    default int defaultSoundEffectVolume() { return 8; }
    //endregion

    //region TTS
    @ConfigSection(
        name = "Text to Speech",
        description = "The options that control the text to speech notifications",
        position = 6,
        closedByDefault = true
    )
    String ttsSection = "ttsSection";

    @ConfigItem(
        keyName = DEFAULT_TTS_VOLUME,
        name = "Default Volume",
        description = "The default volume",
        section = ttsSection
    )
    @Range(min = 0, max = 10)
    default int defaultTTSVolume() { return 5; }

    @ConfigItem(
        keyName = DEFAULT_TTS_SOURCE,
        name = "Default Source",
        description = "The default source (Eleven Labs needs your own API Key)",
        section = ttsSection
    )
    default TTSSource defaultTTSSource() { return TTSSource.LEGACY; }

    @ConfigItem(
        keyName = DEFAULT_TTS_VOICE,
        name = "Default Legacy Voice",
        description = "The default legacy voice",
        section = ttsSection
    )
    default Voice defaultTTSVoice() { return Voice.GEORGE; }

    @ConfigItem(
        keyName = DEFAULT_TTS_RATE,
        name = "Default Rate",
        description = "The default rate",
        section = ttsSection
    )
    @Range(min = 1, max = 5)
    default int defaultTTSRate() { return 1; }

    @ConfigItem(
        keyName = ELEVEN_LABS_API_KEY,
        name = "Eleven Labs API Key",
        description = "Enter your API key",
        section = ttsSection
    )
    default String elevenLabsAPIKey() { return ""; }

    @ConfigItem(
        keyName = DEFAULT_ELEVEN_LABS_VOICE,
        name = "Default Eleven Labs Voice",
        description = "Matches on the name of the voice exactly",
        section = ttsSection
    )
    default String defaultElevenLabsVoice() { return null; }
    //endregion

    //region Request Focus
    @ConfigSection(
        name = "Request Focus",
        description = "The options that control the request focus notifications",
        position = 7,
        closedByDefault = true
    )
    String requestFocusSection = "requestFocusSection";

    @ConfigItem(
        keyName = DEFAULT_FORCE_FOCUS,
        name = "Force Focus",
        description = "Sets the default focus mode to force",
        section = requestFocusSection
    )
    default boolean defaultRequestFocusForce() { return false; }
    //endregion
}
