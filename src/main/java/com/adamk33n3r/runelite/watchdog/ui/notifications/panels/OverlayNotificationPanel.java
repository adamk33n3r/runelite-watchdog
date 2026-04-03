package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overlay;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;

public class OverlayNotificationPanel extends NotificationContentPanel<Overlay> {
    private final ColorPickerManager colorPickerManager;

    public OverlayNotificationPanel(Overlay notification, ColorPickerManager colorPickerManager, Runnable onChange) {
        super(notification, onChange);
        this.colorPickerManager = colorPickerManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        this.add(PanelUtils.createTextField(
            "Enter your formatted message...",
            "",
            this.notification.getMessage(),
            val -> {
                this.notification.setMessage(val);
                this.onChange.run();
            }
        ));

        ColorJButton fgColorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The text color of the notification",
            "Text Color",
            this,
            this.notification.getTextColor(),
            this.colorPickerManager,
            false,
            val -> {
                this.notification.setTextColor(val);
                this.onChange.run();
            }
        );
        this.add(fgColorPicker);

        ColorJButton colorPicker = PanelUtils.createColorPicker(
            "Pick a color",
            "The background color of the notification",
            "Background Color",
            this,
            this.notification.getColor(),
            this.colorPickerManager,
            true,
            val -> {
                this.notification.setColor(val);
                this.onChange.run();
            }
        );
        this.add(colorPicker);

        JPanel fileSub = new JPanel(new BorderLayout(3, 3));
        fileSub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(fileSub);
        fileSub.add(PanelUtils.createFileChooser(null, "Path to the image file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            this.notification.setImagePath(fileChooser.getSelectedFile().getAbsolutePath());
            this.onChange.run();
        }, this.notification.getImagePath(), "Image Files", "png", "jpg"));

        var resizeImageCheckbox = PanelUtils.createCheckbox("Resize", "Resize the image to a standard size", this.notification.isResizeImage(), val -> {
            this.notification.setResizeImage(val);
            this.onChange.run();
        });
        fileSub.add(resizeImageCheckbox, BorderLayout.EAST);

        JPanel sub = new JPanel(new BorderLayout(3, 3));
        sub.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(sub);

        JSpinner displayTime = PanelUtils.createSpinner(this.notification.getTimeToLive(), 1, 999, 1, val -> {
            this.notification.setTimeToLive(val);
            this.onChange.run();
        });
        displayTime.setEnabled(!this.notification.isSticky());
        JPanel displayTimePanel = PanelUtils.createIconComponent(Icons.CLOCK, "Time to display in seconds", displayTime);

        var countDownCheckbox = PanelUtils.createCheckbox("Countdown", "Count down to 0 instead of up to time", this.notification.isCountDown(), val -> {
            this.notification.setCountDown(val);
            this.onChange.run();
        });

        sub.add(displayTimePanel);
        sub.add(countDownCheckbox, BorderLayout.EAST);

        var stickyCheckbox = PanelUtils.createCheckbox("Sticky", "Set the notification to not expire", this.notification.isSticky(), val -> {
            this.notification.setSticky(val);
            displayTime.setEnabled(!val);
            countDownCheckbox.setEnabled(!val);
            this.onChange.run();
        });
        this.add(stickyCheckbox);

        JPanel stickyId = PanelUtils.createTextField(
            "ID for Dismiss Overlay...",
            "",
            this.notification.getId(),
            val -> {
                this.notification.setId(val);
                this.onChange.run();
            }
        );
        this.add(stickyId);
    }
}
