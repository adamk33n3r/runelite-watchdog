package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.config.FlashNotification;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import java.util.Arrays;

public class ScreenFlashNotificationPanel extends NotificationPanel {
    public ScreenFlashNotificationPanel(ScreenFlash screenFlash, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
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

        JComboBox<FlashNotification> flashNotificationSelect = new JComboBox<>(Arrays.stream(FlashNotification.values()).filter(fn -> fn != FlashNotification.DISABLED).toArray(FlashNotification[]::new));
        flashNotificationSelect.setToolTipText("The screen flash mode");
        flashNotificationSelect.setSelectedItem(screenFlash.getFlashNotification());
        flashNotificationSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            list.setToolTipText(value.toString());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        flashNotificationSelect.addActionListener(e -> {
            screenFlash.setFlashNotification(flashNotificationSelect.getItemAt(flashNotificationSelect.getSelectedIndex()));
            onChangeListener.run();
        });
        this.settings.add(flashNotificationSelect);
    }
}
