package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

public class ScreenFlashNotificationPanel extends NotificationContentPanel<ScreenFlash> {
    private final ColorPickerManager colorPickerManager;

    public ScreenFlashNotificationPanel(ScreenFlash notification, ColorPickerManager colorPickerManager, Runnable onChange) {
        super(notification, onChange);
        this.colorPickerManager = colorPickerManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        ColorJButton colorPickerBtn = PanelUtils.createColorPicker(
            "Pick a color",
            "The color to flash the screen",
            "Flash Color",
            this,
            this.notification.getColor(),
            this.colorPickerManager,
            true,
            val -> {
                this.notification.setColor(val);
                this.onChange.run();
            });
        this.add(colorPickerBtn);

        JComboBox<FlashMode> flashModeSelect = new JComboBox<>(FlashMode.values());
        flashModeSelect.setToolTipText("The screen flash mode");
        if (this.notification.getFlashMode() == null) {
            this.notification.setFlashMode(FlashMode.FLASH);
            if (this.notification.getFlashDuration() == 0) {
                this.notification.setFlashDuration(2);
            }
        }
        flashModeSelect.setSelectedItem(this.notification.getFlashMode());
        flashModeSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            list.setToolTipText(value.getTooltip());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        flashModeSelect.addActionListener(e -> {
            this.notification.setFlashMode(flashModeSelect.getItemAt(flashModeSelect.getSelectedIndex()));
            this.onChange.run();
        });
        this.add(flashModeSelect);

        JSpinner flashDuration = PanelUtils.createSpinner(this.notification.getFlashDuration(), 0, 120, 1, val -> {
            this.notification.setFlashDuration(val);
            this.onChange.run();
        });
        this.add(PanelUtils.createIconComponent(Icons.CLOCK, "Duration of flash, use 0 to flash until cancelled", flashDuration));
    }
}
