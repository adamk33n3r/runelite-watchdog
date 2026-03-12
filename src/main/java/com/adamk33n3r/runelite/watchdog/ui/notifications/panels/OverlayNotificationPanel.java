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
import java.awt.*;

public class OverlayNotificationPanel extends MessageNotificationPanel {
    private JPanel displayTime;
    private JPanel stickyId;

    public OverlayNotificationPanel(Overlay notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, true, parentPanel, onChangeListener, onRemove);

        ColorJButton fgColorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The text color of the notification",
            "Text Color",
            this,
            notification.getTextColor(),
            colorPickerManager,
            false,
            val -> {
                notification.setTextColor(val);
                onChangeListener.run();
            }
        );
        this.settings.add(fgColorPicker);

        ColorJButton colorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The background color of the notification",
            "Background Color",
            this,
            notification.getColor(),
            colorPickerManager,
            true,
            val -> {
                notification.setColor(val);
                onChangeListener.run();
            }
        );
        this.settings.add(colorPicker);

        JPanel fileSub = new JPanel(new BorderLayout(3, 3));
        fileSub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(fileSub);
        fileSub.add(PanelUtils.createFileChooser(null, "Path to the image file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            notification.setImagePath(fileChooser.getSelectedFile().getAbsolutePath());
            onChangeListener.run();
        }, notification.getImagePath(), "Image Files", "png", "jpg"));

        var resizeImageCheckbox = PanelUtils.createCheckbox("Resize", "Resize the image to a standard size", notification.isResizeImage(), val -> {
            notification.setResizeImage(val);
            onChangeListener.run();
        });
        fileSub.add(resizeImageCheckbox, BorderLayout.EAST);

        JPanel sub = new JPanel(new BorderLayout(3, 3));
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.settings.add(sub);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getTimeToLive(), 1, 999, 1, val -> {
            notification.setTimeToLive(val);
            onChangeListener.run();
        });
        displayTime.setEnabled(!notification.isSticky());
        this.displayTime = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display in seconds", displayTime);

        var countDownCheckbox = PanelUtils.createCheckbox("Countdown", "Count down to 0 instead of up to time", notification.isCountDown(), val -> {
            notification.setCountDown(val);
            this.revalidate();
            onChangeListener.run();
        });

        sub.add(this.displayTime);
        sub.add(countDownCheckbox, BorderLayout.EAST);

        var stickyCheckbox = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            displayTime.setEnabled(!val);
            countDownCheckbox.setEnabled(!val);
            this.revalidate();
            onChangeListener.run();
        });
        this.settings.add(stickyCheckbox);

        this.stickyId = PanelUtils.createTextField(
            "ID for Dismiss Overlay...",
            "",
            notification.getId(),
            val -> {
                notification.setId(val);
                onChangeListener.run();
            }
        );

        this.settings.add(this.stickyId);
    }
}
