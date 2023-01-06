package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import java.awt.image.BufferedImage;

public class OverheadNotificationPanel extends MessageNotificationPanel {
    private static final ImageIcon CLOCK_ICON;

    static {
        final BufferedImage clockIcon = ImageUtil.loadImageResource(NotificationPanel.class, "clock_icon.png");

        CLOCK_ICON = new ImageIcon(ImageUtil.luminanceOffset(clockIcon, -80));
    }

    public OverheadNotificationPanel(Overhead notification, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
        super(notification, onChangeListener, onRemove);


        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 1, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(CLOCK_ICON, "Time to display overhead in seconds", displayTime));
    }
}
