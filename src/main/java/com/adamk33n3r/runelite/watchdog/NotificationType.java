package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.*;

import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    GAME_MESSAGE("Game Message", "Put a game message in your chat", NotificationCategory.TEXT, GameMessage.class),
    SCREEN_FLASH("Screen Flash", "Flash your screen a custom color", NotificationCategory.OVERLAY, ScreenFlash.class),
    SOUND_EFFECT("Sound Effect", "Play a builtin sound effect", NotificationCategory.AUDIO, SoundEffect.class),
    SOUND("Custom Sound", "Play a custom sound", NotificationCategory.AUDIO, Sound.class),
    TEXT_TO_SPEECH("Text to Speech", "Synthesize speech", NotificationCategory.AUDIO, TextToSpeech.class),
    TRAY_NOTIFICATION("Tray Notification", "Create a tray notification", NotificationCategory.TEXT, TrayNotification.class),
    OVERHEAD("Overhead", "Display overhead text", NotificationCategory.TEXT, Overhead.class),
    OVERLAY("Overlay", "Create an overlay notification", NotificationCategory.OVERLAY, Overlay.class),
    POPUP("Popup", "Create a popup notification (like collection log or league task)", NotificationCategory.OVERLAY, Popup.class),
    SCREEN_MARKER("Screen Marker", "Show a screen marker", NotificationCategory.OVERLAY, ScreenMarker.class),
    OBJECT_MARKER("Object Marker", "Show an object marker", NotificationCategory.OVERLAY, ObjectMarker.class),
    DINK("Dink", "Tell Dink to send a custom notification to your webhook", NotificationCategory.ADVANCED, Dink.class),
    SHORTEST_PATH("Shortest Path", "Sets the Shortest Path plugin's path", NotificationCategory.ADVANCED, ShortestPath.class),
    PLUGIN_MESSAGE("Plugin Message", "Send a message to another plugin", NotificationCategory.ADVANCED, PluginMessage.class),
    PLUGIN_TOGGLE("Plugin Toggle", "Toggle a plugin's enabled state", NotificationCategory.ADVANCED, PluginToggle.class),
    DISMISS_OVERLAY("Dismiss Overlay", "Dismiss a sticky overlay by ID", NotificationCategory.ADVANCED, DismissOverlay.class),
    DISMISS_SCREEN_MARKER("Dismiss Screen Marker", "Dismiss a sticky screen marker by ID", NotificationCategory.ADVANCED, DismissScreenMarker.class),
    DISMISS_OBJECT_MARKER("Dismiss Object Marker", "Dismiss a sticky object marker by ID", NotificationCategory.ADVANCED, DismissObjectMarker.class),
    REQUEST_FOCUS("Request Focus", "Requests focus on the window", NotificationCategory.ADVANCED, RequestFocus.class),
    NOTIFICATION_EVENT("Notification Event", "Fire a NotificationFired event so that other plugins may hook into it e.g. RL Tray Notifications", NotificationCategory.ADVANCED, NotificationEvent.class),
    ;

    private final String name;
    private final String tooltip;
    private final NotificationCategory category;
    private final Class<? extends Notification> implClass;
}
