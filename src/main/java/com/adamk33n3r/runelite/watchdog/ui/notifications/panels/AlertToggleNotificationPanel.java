package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.AlertToggle;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JComboBox;
import javax.swing.JPanel;

@Slf4j
public class AlertToggleNotificationPanel extends NotificationPanel {
    public AlertToggleNotificationPanel(AlertToggle notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.rebuildContent = () -> { this.settings.removeAll(); this.buildContent(this.settings, this.onChangeListener); this.settings.revalidate(); };
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((AlertToggle) this.notification, container, onChange, this.rebuildContent);
    }

    public static void buildContent(AlertToggle notification, JPanel container, Runnable onChange, Runnable rebuild) {
        JComboBox<AlertToggle.ToggleMode> modeSelect = PanelUtils.createSelect(AlertToggle.ToggleMode.values(), notification.getMode(), (selected) -> {
            notification.setMode(selected);
            onChange.run();
            rebuild.run();
        });
        JPanel mode = PanelUtils.createLabeledComponent("Mode", "The toggle mode", modeSelect);
        mode.setBorder(null);
        mode.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(mode);

        container.add(PanelUtils.createRegexMatcher(notification, "Enter alert name pattern", "The pattern to match against the alert name"));
    }
}
