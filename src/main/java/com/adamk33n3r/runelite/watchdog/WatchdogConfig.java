package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;

import net.runelite.api.SoundEffectID;
import net.runelite.client.config.*;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.util.ColorUtil;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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
    String SIDE_PANEL_PRIORITY = "sidePanelPriority";
    String ENABLE_NOTIFICATION_CATEGORIES = "enableNotificationCategories";

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
    String DEFAULT_OVERLAY_RESIZE_IMAGE = "defaultOverlayResizeImage";

    // Popup
    String DEFAULT_POPUP_TEXT_COLOR = "defaultPopupTextColor";

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

    // Markers
    String DEFAULT_SCREEN_MARKER_BORDER_COLOR = "defaultScreenMarkerBorderColor";
    String DEFAULT_SCREEN_MARKER_FILL_COLOR = "defaultScreenMarkerFillColor";
    String DEFAULT_SCREEN_MARKER_BORDER_THICKNESS = "defaultScreenMarkerBorderThickness";
    String DEFAULT_SCREEN_MARKER_DISPLAY_TIME = "defaultScreenMarkerDisplayTime";
    String DEFAULT_SCREEN_MARKER_STICKY = "defaultScreenMarkerSticky";

    String DEFAULT_OBJECT_MARKER_BORDER_COLOR = "defaultObjectMarkerBorderColor";
    String DEFAULT_OBJECT_MARKER_FILL_COLOR = "defaultObjectMarkerFillColor";
    String DEFAULT_OBJECT_MARKER_HULL = "defaultObjectMarkerHull";
    String DEFAULT_OBJECT_MARKER_OUTLINE = "defaultObjectMarkerOutline";
    String DEFAULT_OBJECT_MARKER_CLICKBOX = "defaultObjectMarkerClickbox";
    String DEFAULT_OBJECT_MARKER_TILE = "defaultObjectMarkerTile";
    String DEFAULT_OBJECT_MARKER_BORDER_THICKNESS = "defaultObjectMarkerBorderThickness";
    String DEFAULT_OBJECT_MARKER_FEATHER = "defaultObjectMarkerFeather";
    String DEFAULT_OBJECT_MARKER_DISPLAY_TIME = "defaultObjectMarkerDisplayTime";
    String DEFAULT_OBJECT_MARKER_STICKY = "defaultObjectMarkerSticky";

    // Hotkeys
    String CLEAR_ALL_HOTKEY = "clearAllHotkey";
    String STOP_ALL_PROCESSING_ALERTS_HOTKEY = "stopAllProcessingAlertsHotkey";
    String STOP_ALL_SOUNDS_HOTKEY = "stopAllSoundsHotkey";
    String DISMISS_ALL_OVERLAYS_HOTKEY = "dismissAllOverlaysHotkey";
    String DISMISS_ALL_SCREEN_MARKERS_HOTKEY = "dismissAllScreenMarkersHotkey";
    String DISMISS_ALL_OBJECT_MARKERS_HOTKEY = "dismissAllObjectMarkersHotkey";

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
        keyName = MOUSE_MOVEMENT_CANCELS_FLASH,
        name = "Mouse Movement Cancels",
        description = "Cancel the repeated sounds/flashes with mouse movement as well as click and keyboard"
    )
    default boolean mouseMovementCancels() { return true; }

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
        keyName = SIDE_PANEL_PRIORITY,
        name = "Side Panel Priority",
        description = "Panel icon priority, Lower # = higher pos, Higher # = lower pos "
    )
    @Range(min = Integer.MIN_VALUE)
    default int sidePanelPriority() { return 1; }

    @ConfigItem(
        keyName = ENABLE_NOTIFICATION_CATEGORIES,
        name = "Enable Notification Categories",
        description = "Enables the notification categories in the side panel"
    )
    default boolean enableNotificationCategories() { return true; }

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

    @ConfigItem(
        keyName = DEFAULT_OVERLAY_RESIZE_IMAGE,
        name = "Default Resize Image",
        description = "Whether to resize the image to a standard size",
        section = overlaySection,
        position = 8
    )
    default boolean defaultOverlayResizeImage() { return true; }
    //endregion

    //region Popup
    @ConfigSection(
        name = "Popup",
        description = "The options that control the popup notifications",
        position = 3,
        closedByDefault = true
    )
    String popupSection = "popupSection";

    @ConfigItem(
        keyName = DEFAULT_POPUP_TEXT_COLOR,
        name = "Default Text Color",
        description = "The default text color",
        section = popupSection,
        position = 1
    )
    default Color defaultPopupTextColor() { return null; }
    //endregion

    //region Screen Flash
    @ConfigSection(
        name = "Screen Flash",
        description = "The options that control the screen flash notifications",
        position = 4,
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
        position = 5,
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
        position = 6,
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
        position = 7,
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
    default TTSSource defaultTTSSource() { return TTSSource.ELEVEN_LABS; }

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
        position = 8,
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

    // region Markers

    @ConfigSection(
        name = "Markers",
        description = "The options that control the markers notifications",
        position = 9,
        closedByDefault = true
    )
    String markersSection = "markersSection";

    @ConfigItem(
        keyName = DEFAULT_SCREEN_MARKER_BORDER_COLOR,
        name = "Default Screen Marker Border Color",
        description = "The default border color",
        section = markersSection,
        position = 1
    )
    @Alpha
    default Color defaultScreenMarkerBorderColor() { return Color.GREEN; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_MARKER_FILL_COLOR,
        name = "Default Screen Marker Fill Color",
        description = "The default fill color",
        section = markersSection,
        position = 2
    )
    @Alpha
    default Color defaultScreenMarkerFillColor() { return null; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_MARKER_BORDER_THICKNESS,
        name = "Default Screen Marker Border Thickness",
        description = "The default border thickness",
        section = markersSection,
        position = 3
    )
    default int defaultScreenMarkerBorderThickness() { return 2; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_MARKER_DISPLAY_TIME,
        name = "Default Screen Marker Display Time",
        description = "The default display time",
        section = markersSection,
        position = 4
    )
    @Units(Units.SECONDS)
    default int defaultScreenMarkerDisplayTime() { return 5; }

    @ConfigItem(
        keyName = DEFAULT_SCREEN_MARKER_STICKY,
        name = "Default Screen Marker Sticky",
        description = "The default sticky",
        section = markersSection,
        position = 5
    )
    default boolean defaultScreenMarkerSticky() { return false; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_BORDER_COLOR,
        name = "Default Object Marker Border Color",
        description = "The default border color",
        section = markersSection,
        position = 6
    )
    @Alpha
    default Color defaultObjectMarkerBorderColor() { return Color.YELLOW; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_FILL_COLOR,
        name = "Default Object Marker Fill Color",
        description = "The default fill color",
        section = markersSection,
        position = 7
    )
    @Alpha
    default Color defaultObjectMarkerFillColor() { return null; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_HULL,
        name = "Default Object Marker Hull",
        description = "The default hull",
        section = markersSection,
        position = 8
    )
    default boolean defaultObjectMarkerHull() { return true; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_OUTLINE,
        name = "Default Object Marker Outline",
        description = "The default outline",
        section = markersSection,
        position = 9
    )
    default boolean defaultObjectMarkerOutline() { return false; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_CLICKBOX,
        name = "Default Object Marker Clickbox",
        description = "The default clickbox",
        section = markersSection,
        position = 10
    )
    default boolean defaultObjectMarkerClickbox() { return false; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_TILE,
        name = "Default Object Marker Tile",
        description = "The default tile",
        section = markersSection,
        position = 11
    )
    default boolean defaultObjectMarkerTile() { return false; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_BORDER_THICKNESS,
        name = "Default Object Marker Border Thickness",
        description = "The default border thickness",
        section = markersSection,
        position = 12
    )
    default double defaultObjectMarkerBorderThickness() { return 2.0d; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_FEATHER,
        name = "Default Object Marker Feather",
        description = "The default feather",
        section = markersSection,
        position = 13
    )
    @Range(min = 0, max = 4)
    default int defaultObjectMarkerFeather() { return 0; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_DISPLAY_TIME,
        name = "Default Object Marker Display Time",
        description = "The default display time",
        section = markersSection,
        position = 14
    )
    @Units(Units.SECONDS)
    default int defaultObjectMarkerDisplayTime() { return 5; }

    @ConfigItem(
        keyName = DEFAULT_OBJECT_MARKER_STICKY,
        name = "Default Object Marker Sticky",
        description = "The default sticky",
        section = markersSection,
        position = 15
    )
    default boolean defaultObjectMarkerSticky() { return false; }


    // endregion

    //region Hotkeys
    @ConfigSection(
        name = "Hotkeys",
        description = "The hotkeys to use for various actions",
        position = 10,
        closedByDefault = true
    )
    String hotkeysSection = "hotkeysSection";

    @ConfigItem(
        keyName = CLEAR_ALL_HOTKEY,
        name = "Clear All",
        description = "The hotkey to clear all clearable alerts and notifications",
        section = hotkeysSection
    )
    default Keybind clearAllHotkey() { return new Keybind(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK); }

    @ConfigItem(
        keyName = STOP_ALL_PROCESSING_ALERTS_HOTKEY,
        name = "Stop All Processing Alerts",
        description = "The hotkey to stop all processing alerts",
        section = hotkeysSection
    )
    default Keybind stopAllProcessingAlertsHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
        keyName = STOP_ALL_SOUNDS_HOTKEY,
        name = "Stop All Queued Sounds",
        description = "The hotkey to stop all queued sounds",
        section = hotkeysSection
    )
    default Keybind stopAllQueuedSoundsHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
        keyName = DISMISS_ALL_OVERLAYS_HOTKEY,
        name = "Dismiss All Overlays",
        description = "The hotkey to dismiss all overlays",
        section = hotkeysSection
    )
    default Keybind dismissAllOverlaysHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
        keyName = DISMISS_ALL_SCREEN_MARKERS_HOTKEY,
        name = "Dismiss All Screen Markers",
        description = "The hotkey to dismiss all screen markers",
        section = hotkeysSection
    )
    default Keybind dismissAllScreenMarkersHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
        keyName = DISMISS_ALL_OBJECT_MARKERS_HOTKEY,
        name = "Dismiss All Object Markers",
        description = "The hotkey to dismiss all object markers",
        section = hotkeysSection
    )
    default Keybind dismissAllObjectMarkersHotkey() { return Keybind.NOT_SET; }
    //endregion
}
