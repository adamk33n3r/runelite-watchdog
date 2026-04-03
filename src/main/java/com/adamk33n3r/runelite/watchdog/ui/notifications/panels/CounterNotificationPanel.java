package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Counter;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import java.awt.Font;

@Slf4j
public class CounterNotificationPanel extends NotificationContentPanel<Counter> {

    public CounterNotificationPanel(Counter notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        JLabel updateLabel = new JLabel("<html>This UI will not update live. Back out and re-enter alert to see actual value</html>");
        updateLabel.setFont(new Font(updateLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, updateLabel.getFont().getSize()));
        this.add(updateLabel);

        JSpinner spinner = PanelUtils.createSpinner(this.notification.getValue(), 0, Integer.MAX_VALUE, 1, val -> {
            this.notification.setValue(val);
            this.onChange.run();
        });
        this.add(spinner);
    }
}
