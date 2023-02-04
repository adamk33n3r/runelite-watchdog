package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.NotificationType;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

public abstract class NotificationPanel extends JPanel {
    // worldhopper - arrow down
    // screenmarker - border color icon - pencil
    // screenmarker/timetracking - delete icon - X
    // timetracking - notify - bell
    // timetracking - reset - circle arrow - used for in-focus?
    // timetracking - start - right chevron
    // loottracker - back arrow
    // loottracker - collapsed/expanded
    // info - import cloud
    // info - github
    // config - edit/back

    private static final ImageIcon COLLAPSE_ICON;
    private static final ImageIcon EXPAND_ICON;
    private static final ImageIcon DELETE_ICON;
    private static final ImageIcon DELETE_ICON_HOVER;
    private static final ImageIcon FOREGROUND_ICON;
    private static final ImageIcon FOREGROUND_ICON_HOVER;
    private static final ImageIcon BACKGROUND_ICON;
    private static final ImageIcon BACKGROUND_ICON_HOVER;
    public static final ImageIcon TEST_ICON;
    public static final ImageIcon TEST_ICON_HOVER;
    protected static final ImageIcon VOLUME_ICON;
    protected static final ImageIcon CLOCK_ICON;

    static {
        final BufferedImage collapseImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "collapsed.png");
        final BufferedImage expandedImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "expanded.png");
        final BufferedImage deleteImg = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "delete_icon.png");
        final BufferedImage foregroundImg = ImageUtil.loadImageResource(NotificationPanel.class, "foreground_icon.png");
        final BufferedImage backgroundImg = ImageUtil.loadImageResource(NotificationPanel.class, "background_icon.png");
        final BufferedImage testImg = ImageUtil.loadImageResource(NotificationPanel.class, "test_icon.png");
        final BufferedImage volumeImg = ImageUtil.loadImageResource(NotificationPanel.class, "volume_icon.png");
        final BufferedImage clockIcon = ImageUtil.loadImageResource(NotificationPanel.class, "clock_icon.png");

        COLLAPSE_ICON = new ImageIcon(collapseImg);
        EXPAND_ICON = new ImageIcon(expandedImg);
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(deleteImg, -80));
        FOREGROUND_ICON = new ImageIcon(foregroundImg);
        FOREGROUND_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(foregroundImg, -80));
        BACKGROUND_ICON = new ImageIcon(backgroundImg);
        BACKGROUND_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(backgroundImg, -80));
        TEST_ICON = new ImageIcon(testImg);
        TEST_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(testImg, -80));
        VOLUME_ICON = new ImageIcon(ImageUtil.luminanceOffset(volumeImg, -80));
        CLOCK_ICON = new ImageIcon(ImageUtil.luminanceOffset(clockIcon, -80));
    }

    protected JPanel settings = new JPanel(new StretchedStackedLayout(3, 3));

    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
        BorderFactory.createMatteBorder(5, 10, 5, 0, ColorScheme.DARKER_GRAY_COLOR));

    public NotificationPanel(Notification notification, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(3, 0, 0, 0));
        JPanel container = new JPanel(new StretchedStackedLayout(3, 3));
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel nameWrapper = new JPanel(new BorderLayout(3, 3));
        container.add(nameWrapper);

        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);
        nameWrapper.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createMatteBorder(5, 10, 5, 0, ColorScheme.DARKER_GRAY_COLOR)));
        NotificationType notificationType = notification.getType();
        JLabel nameLabel = new JLabel(notificationType.getName());
        nameLabel.setToolTipText(notificationType.getTooltip());
        nameWrapper.add(nameLabel, BorderLayout.WEST);

        // Right buttons
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightActions.setBorder(new EmptyBorder(4, 0, 0, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.add(rightActions, BorderLayout.EAST);

        JButton focusBtn = PanelUtils.createToggleActionButton(
            FOREGROUND_ICON,
            FOREGROUND_ICON_HOVER,
            BACKGROUND_ICON,
            BACKGROUND_ICON_HOVER,
            "Switch to only fire notification while the game is in the background",
            "Enable notification while the game is in the foreground",
            notification.isFireWhenFocused(),
            btn -> {
                notification.setFireWhenFocused(btn.isSelected());
                onChangeListener.run();
            });
        rightActions.add(focusBtn, BorderLayout.EAST);

        JButton testBtn = PanelUtils.createActionButton(
            TEST_ICON,
            TEST_ICON_HOVER,
            "Test the notification",
            btn -> notification.fireForced(new String[]{ "1", "2", "3", "4", "5" }));
        rightActions.add(testBtn);

        JButton deleteBtn = PanelUtils.createActionButton(
            DELETE_ICON,
            DELETE_ICON_HOVER,
            "Remove this notification",
            onRemove);
        rightActions.add(deleteBtn);

        this.settings.setBorder(new EmptyBorder(5, 10, 5, 10));
        this.settings.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(this.settings);

        this.add(container, BorderLayout.CENTER);
    }
}
