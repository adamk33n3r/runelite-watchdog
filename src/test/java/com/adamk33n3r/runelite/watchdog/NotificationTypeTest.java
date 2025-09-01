package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NotificationType enum contract")
class NotificationTypeTest {

    @Test
    @DisplayName("All enum constants are present and order is stable")
    void constantsPresentAndOrdered() {
        NotificationType[] values = NotificationType.values();
        List<NotificationType> expectedOrder = Arrays.asList(
            NotificationType.GAME_MESSAGE,
            NotificationType.SCREEN_FLASH,
            NotificationType.SOUND_EFFECT,
            NotificationType.SOUND,
            NotificationType.TEXT_TO_SPEECH,
            NotificationType.TRAY_NOTIFICATION,
            NotificationType.OVERHEAD,
            NotificationType.OVERLAY,
            NotificationType.POPUP,
            NotificationType.SCREEN_MARKER,
            NotificationType.OBJECT_MARKER,
            NotificationType.DINK,
            NotificationType.PLUGIN_MESSAGE,
            NotificationType.DISMISS_OVERLAY,
            NotificationType.DISMISS_SCREEN_MARKER,
            NotificationType.DISMISS_OBJECT_MARKER,
            NotificationType.REQUEST_FOCUS,
            NotificationType.NOTIFICATION_EVENT
        );
        assertEquals(expectedOrder.size(), values.length, "Enum size mismatch");
        assertIterableEquals(expectedOrder, Arrays.asList(values), "Enum order/contents changed");
    }

    @Test
    @DisplayName("Getter values match expected metadata for each constant")
    void getterMetadataMatches() {
        // name, tooltip, category, implClass
        assertAll("Metadata",
            () -> {
                NotificationType t = NotificationType.GAME_MESSAGE;
                assertEquals("Game Message", t.getName());
                assertEquals("Put a game message in your chat", t.getTooltip());
                assertEquals(NotificationCategory.TEXT, t.getCategory());
                assertEquals(GameMessage.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.SCREEN_FLASH;
                assertEquals("Screen Flash", t.getName());
                assertEquals("Flash your screen a custom color", t.getTooltip());
                assertEquals(NotificationCategory.OVERLAY, t.getCategory());
                assertEquals(ScreenFlash.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.SOUND_EFFECT;
                assertEquals("Sound Effect", t.getName());
                assertEquals("Play a builtin sound effect", t.getTooltip());
                assertEquals(NotificationCategory.AUDIO, t.getCategory());
                assertEquals(SoundEffect.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.SOUND;
                assertEquals("Custom Sound", t.getName());
                assertEquals("Play a custom sound", t.getTooltip());
                assertEquals(NotificationCategory.AUDIO, t.getCategory());
                assertEquals(Sound.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.TEXT_TO_SPEECH;
                assertEquals("Text to Speech", t.getName());
                assertEquals("Synthesize speech", t.getTooltip());
                assertEquals(NotificationCategory.AUDIO, t.getCategory());
                assertEquals(TextToSpeech.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.TRAY_NOTIFICATION;
                assertEquals("Tray Notification", t.getName());
                assertEquals("Create a tray notification", t.getTooltip());
                assertEquals(NotificationCategory.TEXT, t.getCategory());
                assertEquals(TrayNotification.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.OVERHEAD;
                assertEquals("Overhead", t.getName());
                assertEquals("Display overhead text", t.getTooltip());
                assertEquals(NotificationCategory.TEXT, t.getCategory());
                assertEquals(Overhead.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.OVERLAY;
                assertEquals("Overlay", t.getName());
                assertEquals("Create an overlay notification", t.getTooltip());
                assertEquals(NotificationCategory.OVERLAY, t.getCategory());
                assertEquals(Overlay.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.POPUP;
                assertEquals("Popup", t.getName());
                assertEquals("Create a popup notification (like collection log or league task)", t.getTooltip());
                assertEquals(NotificationCategory.OVERLAY, t.getCategory());
                assertEquals(Popup.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.SCREEN_MARKER;
                assertEquals("Screen Marker", t.getName());
                assertEquals("Show a screen marker", t.getTooltip());
                assertEquals(NotificationCategory.OVERLAY, t.getCategory());
                assertEquals(ScreenMarker.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.OBJECT_MARKER;
                assertEquals("Object Marker", t.getName());
                assertEquals("Show an object marker", t.getTooltip());
                assertEquals(NotificationCategory.OVERLAY, t.getCategory());
                assertEquals(ObjectMarker.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.DINK;
                assertEquals("Dink", t.getName());
                assertEquals("Tell Dink to send a custom notification to your webhook", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(Dink.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.PLUGIN_MESSAGE;
                assertEquals("Plugin Message", t.getName());
                assertEquals("Send a message to another plugin", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(PluginMessage.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.DISMISS_OVERLAY;
                assertEquals("Dismiss Overlay", t.getName());
                assertEquals("Dismiss a sticky overlay by ID", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(DismissOverlay.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.DISMISS_SCREEN_MARKER;
                assertEquals("Dismiss Screen Marker", t.getName());
                assertEquals("Dismiss a sticky screen marker by ID", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(DismissScreenMarker.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.DISMISS_OBJECT_MARKER;
                assertEquals("Dismiss Object Marker", t.getName());
                assertEquals("Dismiss a sticky object marker by ID", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(DismissObjectMarker.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.REQUEST_FOCUS;
                assertEquals("Request Focus", t.getName());
                assertEquals("Requests focus on the window", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(RequestFocus.class, t.getImplClass());
            },
            () -> {
                NotificationType t = NotificationType.NOTIFICATION_EVENT;
                assertEquals("Notification Event", t.getName());
                assertEquals("Fire a NotificationFired event so that other plugins may hook into it e.g. RL Tray Notifications", t.getTooltip());
                assertEquals(NotificationCategory.ADVANCED, t.getCategory());
                assertEquals(NotificationEvent.class, t.getImplClass());
            }
        );
    }

    @Test
    @DisplayName("implClass should implement/extend Notification for all constants")
    void implClassImplementsNotification() {
        for (NotificationType t : NotificationType.values()) {
            assertTrue(Notification.class.isAssignableFrom(t.getImplClass()),
                () -> t.name() + " implClass must be assignable to Notification");
        }
    }

    @Test
    @DisplayName("Enum names (display names) and tooltips are non-empty and unique where applicable")
    void namesTooltipsValidation() {
        Set<String> displayNames = new HashSet<>();
        Set<String> tooltips = new HashSet<>();
        for (NotificationType t : NotificationType.values()) {
            assertNotNull(t.getName(), "name should not be null");
            assertFalse(t.getName().trim().isEmpty(), "name should not be empty");
            assertTrue(displayNames.add(t.getName()), "duplicate display name: " + t.getName());

            assertNotNull(t.getTooltip(), "tooltip should not be null");
            assertFalse(t.getTooltip().trim().isEmpty(), "tooltip should not be empty");
            // Tooltips can duplicate conceptually, but ensure most are unique to catch regressions.
            tooltips.add(t.getTooltip());
        }
        // Soft check: at least half of tooltips are unique
        assertTrue(tooltips.size() >= NotificationType.values().length / 2,
            "Expected a reasonable amount of unique tooltips");
    }

    @Test
    @DisplayName("Categories distribution sanity check")
    void categoryDistribution() {
        Map<NotificationCategory, Long> counts =
            Arrays.stream(NotificationType.values())
                .collect(Collectors.groupingBy(NotificationType::getCategory, Collectors.counting()));

        // Basic expectations from enum declaration
        assertEquals(3L, counts.getOrDefault(NotificationCategory.AUDIO, 0L));
        assertEquals(5L, counts.getOrDefault(NotificationCategory.TEXT, 0L));
        assertEquals(5L, counts.getOrDefault(NotificationCategory.OVERLAY, 0L));
        assertEquals(5L, counts.getOrDefault(NotificationCategory.ADVANCED, 0L));
    }

    @Test
    @DisplayName("toString returns enum constant name")
    void toStringIsConstantName() {
        assertEquals("GAME_MESSAGE", NotificationType.GAME_MESSAGE.toString());
        assertEquals("NOTIFICATION_EVENT", NotificationType.NOTIFICATION_EVENT.toString());
    }
}