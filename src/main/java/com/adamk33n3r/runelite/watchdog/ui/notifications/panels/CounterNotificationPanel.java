package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Counter;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class CounterNotificationPanel extends NotificationPanel {
    public CounterNotificationPanel(Counter notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        this.rebuild();
    }

    private void rebuild() {
        this.settings.removeAll();

        Counter notification = (Counter) this.notification;

        JLabel updateLabel = new JLabel("<html>This UI will not update live. Back out and re-enter alert to see actual value</html>");
        updateLabel.setFont(new Font(updateLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, updateLabel.getFont().getSize()));
        this.settings.add(updateLabel);

        JSpinner spinner = PanelUtils.createSpinner(notification.getValue(), 0, Integer.MAX_VALUE, 1, val -> {
            notification.setValue(val);
            onChangeListener.run();
        });
        this.settings.add(spinner);
    }
}
