package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

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
            true,
            val -> {
                screenFlash.setColor(val);
                onChangeListener.run();
            });
        this.settings.add(colorPickerBtn);

        JComboBox<FlashMode> flashModeSelect = new JComboBox<>(FlashMode.values());
        flashModeSelect.setToolTipText("The screen flash mode");
        // TODO: Would be nice to move this somewhere else on import or something
        if (screenFlash.getFlashMode() == null) {
            screenFlash.setFlashMode(FlashMode.FLASH);
            if (screenFlash.getFlashDuration() == 0) {
                screenFlash.setFlashDuration(2);
            }
        }
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

        JSpinner flashDuration = PanelUtils.createSpinner(screenFlash.getFlashDuration(), 0, 120, 1, val -> {
            screenFlash.setFlashDuration(val);
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(Icons.CLOCK, "Duration of flash, use 0 to flash until cancelled", flashDuration));
    }
}
