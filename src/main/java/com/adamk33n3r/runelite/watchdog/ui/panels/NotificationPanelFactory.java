package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.notifications.*;
import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import com.adamk33n3r.runelite.watchdog.notifications.objectmarkers.ObjectMarker;
import com.adamk33n3r.runelite.watchdog.ui.notifications.panels.*;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Creates {@link NotificationContentPanel} instances for any notification type, and wraps them
 * in a full {@link NotificationPanel} (sidebar chrome) when needed.
 */
@Slf4j
@Singleton
public class NotificationPanelFactory {
    @Inject private ColorPickerManager colorPickerManager;
    @Inject private ConfigManager configManager;
    @Inject private PluginManager pluginManager;
    @Inject private AlertManager alertManager;

    /**
     * Creates a full {@link NotificationPanel} (title bar, enable toggle, delay, test/delete buttons)
     * for the sidebar. Used by {@link NotificationsPanel#addPanel}.
     */
    public NotificationPanel createPanel(Notification notification, NotificationsPanel parent, PanelUtils.OnRemove onRemove) {
        Runnable onChange = alertManager::saveAlerts;
        NotificationContentPanel<?> content = createContentPanel(notification, onChange);
        if (content == null) {
            return null;
        }

        // ScreenMarkerNotificationPanel needs to hook the onRemove to clean up the overlay.
        PanelUtils.OnRemove effectiveOnRemove = (content instanceof ScreenMarkerNotificationPanel)
            ? ((ScreenMarkerNotificationPanel) content).wrapOnRemove(onRemove)
            : onRemove;

        return new NotificationPanel(content, parent, onChange, effectiveOnRemove);
    }

    /**
     * Creates just the type-specific content panel for the given notification.
     * Node panels add this directly to their {@code items} container.
     *
     * @param notification the notification to create a content panel for
     * @param onChange     callback fired whenever the user changes a value
     * @return the content panel, or {@code null} if the type is not registered
     */
    public NotificationContentPanel<?> createContentPanel(Notification notification, Runnable onChange) {
        if (notification instanceof GameMessage)
            return new MessageNotificationPanel((GameMessage) notification, onChange);
        if (notification instanceof TextToSpeech)
            return new TextToSpeechNotificationPanel((TextToSpeech) notification, onChange);
        if (notification instanceof Sound)
            return new SoundNotificationPanel((Sound) notification, onChange);
        if (notification instanceof SoundEffect)
            return new SoundEffectNotificationPanel((SoundEffect) notification, onChange);
        if (notification instanceof TrayNotification)
            return new MessageNotificationPanel((TrayNotification) notification, onChange);
        if (notification instanceof ScreenFlash)
            return new ScreenFlashNotificationPanel((ScreenFlash) notification, colorPickerManager, onChange);
        if (notification instanceof Overhead)
            return new OverheadNotificationPanel((Overhead) notification, colorPickerManager, onChange);
        if (notification instanceof Overlay)
            return new OverlayNotificationPanel((Overlay) notification, colorPickerManager, onChange);
        if (notification instanceof Popup)
            return new PopupNotificationPanel((Popup) notification, colorPickerManager, onChange);
        if (notification instanceof RequestFocus)
            return new RequestFocusNotificationPanel((RequestFocus) notification, onChange);
        if (notification instanceof NotificationEvent)
            return new MessageNotificationPanel((NotificationEvent) notification, onChange);
        if (notification instanceof DismissOverlay)
            return new DismissOverlayNotificationPanel((DismissOverlay) notification, onChange);
        if (notification instanceof DismissScreenMarker)
            return new DismissScreenMarkerNotificationPanel((DismissScreenMarker) notification, onChange);
        if (notification instanceof DismissObjectMarker)
            return new DismissObjectMarkerNotificationPanel((DismissObjectMarker) notification, onChange);
        if (notification instanceof ScreenMarker)
            return new ScreenMarkerNotificationPanel((ScreenMarker) notification, colorPickerManager, onChange);
        if (notification instanceof ObjectMarker)
            return new ObjectMarkerNotificationPanel((ObjectMarker) notification, colorPickerManager, onChange);
        if (notification instanceof Dink)
            return new DinkNotificationPanel((Dink) notification, configManager, onChange);
        if (notification instanceof ShortestPath)
            return new ShortestPathNotificationPanel((ShortestPath) notification, configManager, onChange);
        if (notification instanceof PluginMessage)
            return new PluginMessageNotificationPanel((PluginMessage) notification, onChange);
        if (notification instanceof PluginToggle)
            return new PluginToggleNotificationPanel((PluginToggle) notification, pluginManager, onChange);
        if (notification instanceof AlertToggle)
            return new AlertToggleNotificationPanel((AlertToggle) notification, onChange);
        if (notification instanceof Counter)
            return new CounterNotificationPanel((Counter) notification, onChange);

        log.warn("No NotificationContentPanel registered for type: {}", notification.getClass().getSimpleName());
        return null;
    }
}
