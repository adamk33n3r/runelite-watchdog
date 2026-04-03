package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.AlertToggle;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JComboBox;
import javax.swing.JPanel;

@Slf4j
public class AlertToggleNotificationPanel extends NotificationContentPanel<AlertToggle> {

    public AlertToggleNotificationPanel(AlertToggle notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        JComboBox<AlertToggle.ToggleMode> modeSelect = PanelUtils.createSelect(AlertToggle.ToggleMode.values(), this.notification.getMode(), selected -> {
            this.notification.setMode(selected);
            this.onChange.run();
            this.rebuild();
        });
        JPanel mode = PanelUtils.createLabeledComponent("Mode", "The toggle mode", modeSelect);
        mode.setBorder(null);
        mode.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(mode);

        this.add(PanelUtils.createRegexMatcher(this.notification, "Enter alert name pattern", "The pattern to match against the alert name"));
    }
}
