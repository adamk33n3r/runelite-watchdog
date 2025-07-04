package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    GAME_MESSAGE("Game Message", "Put a game message in your chat", GameMessage.class),
    SCREEN_FLASH("Screen Flash", "Flash your screen a custom color", ScreenFlash.class),
    SOUND_EFFECT("Sound Effect", "Play a builtin sound effect", SoundEffect.class),
    SOUND("Custom Sound", "Play a custom sound", Sound.class),
    TEXT_TO_SPEECH("Text to Speech", "Synthesize speech", TextToSpeech.class),
    TRAY_NOTIFICATION("Tray Notification", "Create a tray notification", TrayNotification.class),
    OVERHEAD("Overhead", "Display overhead text", Overhead.class),
    OVERLAY("Overlay", "Create an overlay notification", Overlay.class),
    POPUP("Popup", "Create a popup notification (like collection log or league task)", Popup.class),
    SCREEN_MARKER("Screen Marker", "Show a screen marker", ScreenMarker.class),
    DINK("Discord via Dink", "Tells Dink to send a custom notification to your webhook", Dink.class),
    PLUGIN_MESSAGE("Plugin Message", "Send a message to another plugin", PluginMessage.class),
    DISMISS_OVERLAY("Dismiss Overlay", "Dismiss a sticky overlay by ID", DismissOverlay.class),
    DISMISS_SCREEN_MARKER("Dismiss Screen Marker", "Dismiss a sticky screen marker by ID", DismissScreenMarker.class),
    REQUEST_FOCUS("Request Focus", "Requests focus on the window", RequestFocus.class),
    NOTIFICATION_EVENT("Notification Event", "Fire a NotificationFired event so that other plugins may hook into it e.g. RL Tray Notifications", NotificationEvent.class),
    ;

    private final String name;
    private final String tooltip;
    private final Class<? extends Notification> implClass;
}
