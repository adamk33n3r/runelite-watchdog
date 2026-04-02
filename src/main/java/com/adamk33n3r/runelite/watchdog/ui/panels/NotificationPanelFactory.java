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
import javax.swing.JPanel;

/**
 * Single place for constructing notification panels (with full chrome) or populating
 * just the type-specific content into any JPanel target (e.g. node graph nodes).
 */
@Slf4j
@Singleton
public class NotificationPanelFactory {
    @Inject private ColorPickerManager colorPickerManager;
    @Inject private ConfigManager configManager;
    @Inject private PluginManager pluginManager;
    @Inject private AlertManager alertManager;

    /**
     * Creates a full {@link NotificationPanel} (title bar, enable toggle, delay, test/delete buttons).
     * Replaces the if-chain that previously lived in {@link NotificationsPanel#addPanel}.
     */
    public NotificationPanel createPanel(Notification notification, NotificationsPanel parent, PanelUtils.OnRemove onRemove) {
        Runnable onChange = alertManager::saveAlerts;
        if (notification instanceof GameMessage)
            return new MessageNotificationPanel((GameMessage) notification, true, parent, onChange, onRemove);
        if (notification instanceof TextToSpeech)
            return new TextToSpeechNotificationPanel((TextToSpeech) notification, parent, onChange, onRemove);
        if (notification instanceof Sound)
            return new SoundNotificationPanel((Sound) notification, parent, onChange, onRemove);
        if (notification instanceof SoundEffect)
            return new SoundEffectNotificationPanel((SoundEffect) notification, parent, onChange, onRemove);
        if (notification instanceof TrayNotification)
            return new MessageNotificationPanel((TrayNotification) notification, parent, onChange, onRemove);
        if (notification instanceof ScreenFlash)
            return new ScreenFlashNotificationPanel((ScreenFlash) notification, parent, colorPickerManager, onChange, onRemove);
        if (notification instanceof Overhead)
            return new OverheadNotificationPanel((Overhead) notification, parent, colorPickerManager, onChange, onRemove);
        if (notification instanceof Overlay)
            return new OverlayNotificationPanel((Overlay) notification, parent, colorPickerManager, onChange, onRemove);
        if (notification instanceof Popup)
            return new PopupNotificationPanel((Popup) notification, parent, colorPickerManager, onChange, onRemove);
        if (notification instanceof RequestFocus)
            return new RequestFocusNotificationPanel((RequestFocus) notification, parent, onChange, onRemove);
        if (notification instanceof NotificationEvent)
            return new MessageNotificationPanel((NotificationEvent) notification, parent, onChange, onRemove);
        if (notification instanceof DismissOverlay)
            return new DismissOverlayNotificationPanel((DismissOverlay) notification, parent, onChange, onRemove);
        if (notification instanceof DismissScreenMarker)
            return new DismissScreenMarkerNotificationPanel((DismissScreenMarker) notification, parent, onChange, onRemove);
        if (notification instanceof DismissObjectMarker)
            return new DismissObjectMarkerNotificationPanel((DismissObjectMarker) notification, parent, onChange, onRemove);
        if (notification instanceof ScreenMarker)
            return new ScreenMarkerNotificationPanel((ScreenMarker) notification, parent, colorPickerManager, onChange, onRemove);
        if (notification instanceof ObjectMarker)
            return new ObjectMarkerNotificationPanel((ObjectMarker) notification, parent, colorPickerManager, onChange, onRemove);
        if (notification instanceof Dink)
            return new DinkNotificationPanel((Dink) notification, parent, configManager, onChange, onRemove);
        if (notification instanceof ShortestPath)
            return new ShortestPathNotificationPanel((ShortestPath) notification, parent, configManager, onChange, onRemove);
        if (notification instanceof PluginMessage)
            return new PluginMessageNotificationPanel((PluginMessage) notification, parent, onChange, onRemove);
        if (notification instanceof PluginToggle)
            return new PluginToggleNotificationPanel((PluginToggle) notification, parent, pluginManager, onChange, onRemove);
        if (notification instanceof AlertToggle)
            return new AlertToggleNotificationPanel((AlertToggle) notification, parent, onChange, onRemove);
        if (notification instanceof Counter)
            return new CounterNotificationPanel((Counter) notification, parent, onChange, onRemove);

        log.warn("No NotificationPanel registered for type: {}", notification.getClass().getSimpleName());
        return null;
    }

    /**
     * Populates just the type-specific controls into {@code container} calling {@code onChange} on
     * every mutation. No chrome (no enable toggle, no delay config, no test/delete buttons).
     * Used by node graph panels.
     */
    public void populateContent(Notification notification, JPanel container, Runnable onChange) {
        if (notification instanceof GameMessage) {
            MessageNotificationPanel.buildContent((GameMessage) notification, true, container, onChange);
        } else if (notification instanceof TextToSpeech) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); TextToSpeechNotificationPanel.buildContent((TextToSpeech) notification, container, onChange, holder[0]); container.revalidate(); container.repaint(); };
            TextToSpeechNotificationPanel.buildContent((TextToSpeech) notification, container, onChange, holder[0]);
        } else if (notification instanceof Sound) {
            SoundNotificationPanel.buildContent((Sound) notification, container, onChange);
        } else if (notification instanceof SoundEffect) {
            SoundEffectNotificationPanel.buildContent((SoundEffect) notification, container, onChange);
        } else if (notification instanceof TrayNotification) {
            MessageNotificationPanel.buildContent((TrayNotification) notification, false, container, onChange);
        } else if (notification instanceof ScreenFlash) {
            ScreenFlashNotificationPanel.buildContent((ScreenFlash) notification, container, onChange, colorPickerManager);
        } else if (notification instanceof Overhead) {
            OverheadNotificationPanel.buildContent((Overhead) notification, container, onChange, colorPickerManager);
        } else if (notification instanceof Overlay) {
            OverlayNotificationPanel.buildContent((Overlay) notification, container, onChange, colorPickerManager);
        } else if (notification instanceof Popup) {
            PopupNotificationPanel.buildContent((Popup) notification, container, onChange, colorPickerManager);
        } else if (notification instanceof RequestFocus) {
            RequestFocusNotificationPanel.buildContent((RequestFocus) notification, container, onChange);
        } else if (notification instanceof NotificationEvent) {
            MessageNotificationPanel.buildContent((NotificationEvent) notification, false, container, onChange);
        } else if (notification instanceof DismissOverlay) {
            DismissOverlayNotificationPanel.buildContent((DismissOverlay) notification, container, onChange);
        } else if (notification instanceof DismissScreenMarker) {
            DismissScreenMarkerNotificationPanel.buildContent((DismissScreenMarker) notification, container, onChange);
        } else if (notification instanceof DismissObjectMarker) {
            DismissObjectMarkerNotificationPanel.buildContent((DismissObjectMarker) notification, container, onChange);
        } else if (notification instanceof ScreenMarker) {
            // ScreenMarker requires instance state — create a panel and delegate its instance buildContent
            log.warn("ScreenMarker populateContent: uses instance-stateful panel; limited support in node context");
        } else if (notification instanceof ObjectMarker) {
            log.warn("ObjectMarker populateContent: uses instance-stateful panel; limited support in node context");
        } else if (notification instanceof Dink) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); DinkNotificationPanel.buildContent((Dink) notification, container, onChange, holder[0], configManager); container.revalidate(); container.repaint(); };
            DinkNotificationPanel.buildContent((Dink) notification, container, onChange, holder[0], configManager);
        } else if (notification instanceof ShortestPath) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); ShortestPathNotificationPanel.buildContent((ShortestPath) notification, container, onChange, holder[0], configManager); container.revalidate(); container.repaint(); };
            ShortestPathNotificationPanel.buildContent((ShortestPath) notification, container, onChange, holder[0], configManager);
        } else if (notification instanceof PluginMessage) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); PluginMessageNotificationPanel.buildContent((PluginMessage) notification, container, onChange, holder[0]); container.revalidate(); container.repaint(); };
            PluginMessageNotificationPanel.buildContent((PluginMessage) notification, container, onChange, holder[0]);
        } else if (notification instanceof PluginToggle) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); PluginToggleNotificationPanel.buildContent((PluginToggle) notification, container, onChange, holder[0], pluginManager); container.revalidate(); container.repaint(); };
            PluginToggleNotificationPanel.buildContent((PluginToggle) notification, container, onChange, holder[0], pluginManager);
        } else if (notification instanceof AlertToggle) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); AlertToggleNotificationPanel.buildContent((AlertToggle) notification, container, onChange, holder[0]); container.revalidate(); container.repaint(); };
            AlertToggleNotificationPanel.buildContent((AlertToggle) notification, container, onChange, holder[0]);
        } else if (notification instanceof Counter) {
            Runnable[] holder = {null};
            holder[0] = () -> { container.removeAll(); CounterNotificationPanel.buildContent((Counter) notification, container, onChange, holder[0]); container.revalidate(); container.repaint(); };
            CounterNotificationPanel.buildContent((Counter) notification, container, onChange, holder[0]);
        } else {
            log.warn("No content builder registered for notification type: {}", notification.getClass().getSimpleName());
        }
    }
}
