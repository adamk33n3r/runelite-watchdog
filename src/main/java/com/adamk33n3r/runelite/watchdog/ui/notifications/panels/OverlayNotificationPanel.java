package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overlay;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class OverlayNotificationPanel extends MessageNotificationPanel {
    private JPanel displayTime;

    public OverlayNotificationPanel(Overlay notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        ColorJButton fgColorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The foreground color of the notification",
            "Foreground Color",
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

        this.settings.add(PanelUtils.createFileChooser(null, "Path to the image file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            notification.setImagePath(fileChooser.getSelectedFile().getAbsolutePath());
            onChangeListener.run();
        }, notification.getImagePath(), "Image Files", "png", "jpg"));

        JCheckBox sticky = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", notification.isSticky(), val -> {
            notification.setSticky(val);
            if (val) {
                this.settings.remove(this.displayTime);
            } else {
                this.settings.add(this.displayTime);
            }
            this.revalidate();
            onChangeListener.run();
        });
        this.settings.add(sticky);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getTimeToLive(), 1, 99, 1, val -> {
            notification.setTimeToLive(val);
            onChangeListener.run();
        });
        this.displayTime = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display in seconds", displayTime);

        if (!notification.isSticky()) {
            this.settings.add(this.displayTime);
        }
    }
}
