package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.AlertToggle;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class AlertToggleNotificationPanel extends NotificationPanel {
    public AlertToggleNotificationPanel(AlertToggle notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        this.rebuild();
    }

    private void rebuild() {
        this.settings.removeAll();

        AlertToggle notification = (AlertToggle) this.notification;

        JComboBox<AlertToggle.ToggleMode> modeSelect = PanelUtils.createSelect(AlertToggle.ToggleMode.values(), notification.getMode(), (selected) -> {
            notification.setMode(selected);
            onChangeListener.run();
            this.rebuild();
        });
        JPanel mode = PanelUtils.createLabeledComponent("Mode", "The toggle mode", modeSelect);
        mode.setBorder(null);
        mode.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(mode);

        this.settings.add(PanelUtils.createRegexMatcher(notification, "Enter alert name pattern", "The pattern to match against the alert name"));
    }
}
