package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overlay;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;

public class OverlayNotificationPanel extends MessageNotificationPanel {
    private final ColorPickerManager colorPickerManager;

    public OverlayNotificationPanel(Overlay notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, true, parentPanel, onChangeListener, onRemove);
        this.colorPickerManager = colorPickerManager;
        // MessageNotificationPanel already added the text area; add Overlay-specific controls
        buildExtraContent(notification, this.settings, onChangeListener, colorPickerManager);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((Overlay) this.notification, container, onChange, this.colorPickerManager);
    }

    public static void buildContent(Overlay notification, JPanel container, Runnable onChange, ColorPickerManager colorPickerManager) {
        MessageNotificationPanel.buildContent(notification, true, container, onChange);
        buildExtraContent(notification, container, onChange, colorPickerManager);
    }

    private static void buildExtraContent(Overlay notification, JPanel container, Runnable onChange, ColorPickerManager colorPickerManager) {
        ColorJButton fgColorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The text color of the notification",
            "Text Color",
            container,
            notification.getTextColor(),
            colorPickerManager,
            false,
            val -> {
                notification.setTextColor(val);
                onChange.run();
            }
        );
        container.add(fgColorPicker);

        ColorJButton colorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The background color of the notification",
            "Background Color",
            container,
            notification.getColor(),
            colorPickerManager,
            true,
            val -> {
                notification.setColor(val);
                onChange.run();
            }
        );
        container.add(colorPicker);

        JPanel fileSub = new JPanel(new BorderLayout(3, 3));
        fileSub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(fileSub);
        fileSub.add(PanelUtils.createFileChooser(null, "Path to the image file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            notification.setImagePath(fileChooser.getSelectedFile().getAbsolutePath());
            onChange.run();
        }, notification.getImagePath(), "Image Files", "png", "jpg"));

        var resizeImageCheckbox = PanelUtils.createCheckbox("Resize", "Resize the image to a standard size", notification.isResizeImage(), val -> {
            notification.setResizeImage(val);
            onChange.run();
        });
        fileSub.add(resizeImageCheckbox, BorderLayout.EAST);

        JPanel sub = new JPanel(new BorderLayout(3, 3));
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.add(sub);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getTimeToLive(), 1, 999, 1, val -> {
            notification.setTimeToLive(val);
            onChange.run();
        });
        displayTime.setEnabled(!notification.isSticky());
        JPanel displayTimePanel = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display in seconds", displayTime);

        var countDownCheckbox = PanelUtils.createCheckbox("Countdown", "Count down to 0 instead of up to time", notification.isCountDown(), val -> {
            notification.setCountDown(val);
            onChange.run();
        });

        sub.add(displayTimePanel);
        sub.add(countDownCheckbox, BorderLayout.EAST);

        var stickyCheckbox = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            displayTime.setEnabled(!val);
            countDownCheckbox.setEnabled(!val);
            onChange.run();
        });
        container.add(stickyCheckbox);

        JPanel stickyId = PanelUtils.createTextField(
            "ID for Dismiss Overlay...",
            "",
            notification.getId(),
            val -> {
                notification.setId(val);
                onChange.run();
            }
        );
        container.add(stickyId);
    }
}
