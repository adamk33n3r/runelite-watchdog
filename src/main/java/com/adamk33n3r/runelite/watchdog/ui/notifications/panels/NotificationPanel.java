package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.ui.StretchedStackedLayout;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
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
    private static final ImageIcon FOCUS_ICON;
    private static final ImageIcon FOCUS_ICON_HOVER;
    private static final ImageIcon FOCUS_SELECTED_ICON;
    private static final ImageIcon FOCUS_SELECTED_ICON_HOVER;
    protected static final ImageIcon VOLUME_ICON;

    static {
        final BufferedImage collapseImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "collapsed.png");
        final BufferedImage expandedImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "expanded.png");
        final BufferedImage focusImg = ImageUtil.loadImageResource(NotificationPanel.class, "focus_icon.png");
        final BufferedImage volumeImg = ImageUtil.loadImageResource(NotificationPanel.class, "volume_icon.png");

        COLLAPSE_ICON = new ImageIcon(collapseImg);
        EXPAND_ICON = new ImageIcon(expandedImg);
        FOCUS_ICON = new ImageIcon(ImageUtil.luminanceOffset(focusImg, -80));
        FOCUS_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(focusImg, -120));
        FOCUS_SELECTED_ICON = new ImageIcon(focusImg);
        FOCUS_SELECTED_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(focusImg, -80));
        VOLUME_ICON = new ImageIcon(ImageUtil.luminanceOffset(volumeImg, -80));
    }

    protected JPanel container = new JPanel(new StretchedStackedLayout(3, 3));
    protected JPanel footer = new JPanel(new DynamicGridLayout(1, 0, 3, 3));

    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
        BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR, 5));

    public NotificationPanel(Notification notification) {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(3, 10, 0, 10));
//        this.setBorder(new TitledBorder(new EtchedBorder(), Util.humanReadableClass(notification), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
//        this.setBackground(ColorScheme.PROGRESS_ERROR_COLOR);
        this.container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel nameWrapper = new JPanel(new BorderLayout(3, 3));
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

        JButton focusBtn = new JButton();
        SwingUtil.removeButtonDecorations(focusBtn);
        focusBtn.setIcon(FOCUS_ICON);
        focusBtn.setRolloverIcon(FOCUS_ICON_HOVER);
        focusBtn.setSelectedIcon(FOCUS_SELECTED_ICON);
        focusBtn.setRolloverSelectedIcon(FOCUS_SELECTED_ICON_HOVER);
        SwingUtil.addModalTooltip(focusBtn, "Set only out of focus", "Set allow in-focus");
        focusBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        focusBtn.setUI(new BasicButtonUI()); // substance breaks the layout
        focusBtn.addActionListener(ev -> {
            focusBtn.setSelected(!focusBtn.isSelected());
            notification.setFireWhenFocused(focusBtn.isSelected());
        });
        nameWrapper.add(focusBtn, BorderLayout.EAST);
        this.container.add(nameWrapper);

        this.footer.setBackground(ColorScheme.PROGRESS_ERROR_COLOR);

        this.add(container, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);

//        JCheckBox fireWhenFocused = new JCheckBox("Fire when in focus", notification.isFireWhenFocused());
//        fireWhenFocused.setToolTipText("Allows the notification to fire when the game is focused");
//        fireWhenFocused.addChangeListener(ev -> {
//            notification.setFireWhenFocused(fireWhenFocused.isSelected());
//        });
//        this.container.add(fireWhenFocused);

        JButton testButton = new JButton("Test");
        testButton.setToolTipText("Test the notification");
        testButton.addActionListener(ev -> {
            boolean prev = notification.isFireWhenFocused();
            notification.setFireWhenFocused(true);
            notification.fire();
            notification.setFireWhenFocused(prev);
        });
        this.footer.add(testButton);

        JButton remove = new JButton("Remove");
                remove.setToolTipText("Remove this notification");
                remove.addActionListener(ev -> {
//            this.notifications.remove(notification);
//            this.notificationContainer.remove(notificationPanel);
//            this.notificationContainer.revalidate();
        });
        this.footer.add(remove);

    }
}
