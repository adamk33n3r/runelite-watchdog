package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Counter;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.Font;

@Slf4j
public class CounterNotificationPanel extends NotificationPanel {
    public CounterNotificationPanel(Counter notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.rebuildContent = () -> { this.settings.removeAll(); this.buildContent(this.settings, this.onChangeListener); this.settings.revalidate(); };
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((Counter) this.notification, container, onChange, this.rebuildContent);
    }

    public static void buildContent(Counter notification, JPanel container, Runnable onChange, Runnable rebuild) {
        JLabel updateLabel = new JLabel("<html>This UI will not update live. Back out and re-enter alert to see actual value</html>");
        updateLabel.setFont(new Font(updateLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, updateLabel.getFont().getSize()));
        container.add(updateLabel);

        JSpinner spinner = PanelUtils.createSpinner(notification.getValue(), 0, Integer.MAX_VALUE, 1, val -> {
            notification.setValue(val);
            onChange.run();
        });
        container.add(spinner);
    }
}
