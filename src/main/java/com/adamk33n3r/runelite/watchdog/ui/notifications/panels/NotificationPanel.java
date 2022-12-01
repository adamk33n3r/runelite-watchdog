package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
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
    private static final ImageIcon FOCUS_ICON;
    private static final ImageIcon FOCUS_ICON_HOVER;
    private static final ImageIcon FOCUS_SELECTED_ICON;
    private static final ImageIcon FOCUS_SELECTED_ICON_HOVER;
    private static final ImageIcon TEST_ICON;
    private static final ImageIcon TEST_ICON_HOVER;
    protected static final ImageIcon VOLUME_ICON;

    static {
        final BufferedImage collapseImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "collapsed.png");
        final BufferedImage expandedImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "expanded.png");
        final BufferedImage deleteImg = ImageUtil.loadImageResource(TimeTrackingPlugin.class, "delete_icon.png");
        final BufferedImage focusImg = ImageUtil.loadImageResource(NotificationPanel.class, "focus_icon.png");
        final BufferedImage testImg = ImageUtil.loadImageResource(NotificationPanel.class, "test_icon.png");
        final BufferedImage volumeImg = ImageUtil.loadImageResource(NotificationPanel.class, "volume_icon.png");

        COLLAPSE_ICON = new ImageIcon(collapseImg);
        EXPAND_ICON = new ImageIcon(expandedImg);
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(deleteImg, -80));
        FOCUS_ICON = new ImageIcon(ImageUtil.luminanceOffset(focusImg, -80));
        FOCUS_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(focusImg, -120));
        FOCUS_SELECTED_ICON = new ImageIcon(focusImg);
        FOCUS_SELECTED_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(focusImg, -80));
        TEST_ICON = new ImageIcon(testImg);
        TEST_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(testImg, -80));
        VOLUME_ICON = new ImageIcon(ImageUtil.luminanceOffset(volumeImg, -80));
    }

    protected JPanel container = new JPanel(new StretchedStackedLayout(3, 3));

    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
        BorderFactory.createMatteBorder(5, 0, 5, 0, ColorScheme.DARKER_GRAY_COLOR));

    public NotificationPanel(Notification notification, PanelUtils.ButtonClickListener onRemove) {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(3, 0, 0, 0));
//        this.setBorder(new TitledBorder(new EtchedBorder(), Util.humanReadableClass(notification), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
//        this.setBackground(ColorScheme.PROGRESS_ERROR_COLOR);
        this.container.setBorder(new EmptyBorder(0, 5, 0, 5));
        this.container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel nameWrapper = new JPanel(new BorderLayout(3, 3));
        this.container.add(nameWrapper);
//        nameWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);
        nameWrapper.add(new JLabel(Util.humanReadableClass(notification)), BorderLayout.WEST);
        JButton collapseBtn = new JButton();
        SwingUtil.removeButtonDecorations(collapseBtn);
        collapseBtn.setIcon(EXPAND_ICON);
//        checkbox?
//            make them draggable
        collapseBtn.setSelectedIcon(COLLAPSE_ICON);
        SwingUtil.addModalTooltip(collapseBtn, "Collapse", "Un-Collapse");
        collapseBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        collapseBtn.setUI(new BasicButtonUI()); // substance breaks the layout
        collapseBtn.addActionListener(ev -> collapseBtn.setSelected(!collapseBtn.isSelected()));
//        nameWrapper.add(collapseBtn, BorderLayout.CENTER);


        // Right buttons
//        JPanel rightActions = new JPanel(new DynamicGridLayout(1, 0, 3, 3));
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightActions.setBorder(new EmptyBorder(4, 0, 0, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.add(rightActions, BorderLayout.EAST);

        JButton focusBtn = PanelUtils.createToggleActionButton(FOCUS_SELECTED_ICON,
            FOCUS_SELECTED_ICON_HOVER,
            FOCUS_ICON,
            FOCUS_ICON_HOVER,
            "Set only out of Focus",
            "Set allow in-focus",
            btn -> notification.setFireWhenFocused(btn.isSelected()));
        rightActions.add(focusBtn, BorderLayout.EAST);

        JButton testBtn = PanelUtils.createActionButton(TEST_ICON,
            TEST_ICON_HOVER,
            "Test the notification",
            btn -> notification.fireForced());
        rightActions.add(testBtn);

        JButton deleteBtn = PanelUtils.createActionButton(DELETE_ICON,
            DELETE_ICON_HOVER,
            "Remove this notification",
            onRemove);
        rightActions.add(deleteBtn);

        this.add(container, BorderLayout.CENTER);
    }
}
