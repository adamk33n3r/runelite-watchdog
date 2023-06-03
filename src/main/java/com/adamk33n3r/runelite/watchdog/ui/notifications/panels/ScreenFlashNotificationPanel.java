package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.config.FlashNotification;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import java.util.Arrays;

public class ScreenFlashNotificationPanel extends NotificationPanel {
    public ScreenFlashNotificationPanel(ScreenFlash screenFlash, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(screenFlash, parentPanel, onChangeListener, onRemove);

        ColorJButton colorPickerBtn = PanelUtils.createColorPicker(
            "Pick a color",
            "The color to flash the screen",
            "Flash Color",
            this,
            screenFlash.getColor(),
            colorPickerManager,
            val -> {
                screenFlash.setColor(val);
                onChangeListener.run();
            });
        this.settings.add(colorPickerBtn);

        JComboBox<FlashMode> flashModeSelect = new JComboBox<>(FlashMode.values());
        flashModeSelect.setToolTipText("The screen flash mode");
        flashModeSelect.setSelectedItem(screenFlash.getFlashMode());
        flashModeSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            list.setToolTipText(value.getTooltip());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        flashModeSelect.addActionListener(e -> {
            screenFlash.setFlashMode(flashModeSelect.getItemAt(flashModeSelect.getSelectedIndex()));
            onChangeListener.run();
        });
        this.settings.add(flashModeSelect);

        JSpinner flashDuration = PanelUtils.createSpinner(screenFlash.getFlashDuration(), 0, 10, 1, val -> {
            screenFlash.setFlashDuration(val);
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(CLOCK_ICON, "Duration of flash, use 0 to flash until cancelled", flashDuration));
    }
}
