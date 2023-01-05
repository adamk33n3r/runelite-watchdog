package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.config.FlashNotification;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class ScreenFlashNotificationPanel extends NotificationPanel {
    public ScreenFlashNotificationPanel(ScreenFlash screenFlash, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
        super(screenFlash, onChangeListener, onRemove);


        ColorJButton colorPickerBtn;
        Color existing = screenFlash.color;
        if (existing == null) {
            colorPickerBtn = new ColorJButton("Pick a color", Color.BLACK);
        } else {
            String colorHex = "#" + ColorUtil.colorToAlphaHexCode(existing);
            colorPickerBtn = new ColorJButton(colorHex, existing);
        }
        colorPickerBtn.setToolTipText("The color to flash the screen");
        colorPickerBtn.setFocusable(false);
        colorPickerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RuneliteColorPicker colorPicker = colorPickerManager.create(
                    SwingUtilities.getWindowAncestor(ScreenFlashNotificationPanel.this),
                    colorPickerBtn.getColor(),
                    "Flash Color",
                    false);
//                colorPicker.setLocation(getLocationOnScreen());
                colorPicker.setOnColorChange(c -> {
                    colorPickerBtn.setColor(c);
                    colorPickerBtn.setText("#" + ColorUtil.colorToAlphaHexCode(c).toUpperCase());
                });
                colorPicker.setOnClose(c -> {
                    screenFlash.color = c;
                    onChangeListener.run();
                });
                colorPicker.setVisible(true);
            }
        });
        this.settings.add(colorPickerBtn);

        JComboBox<FlashNotification> flashNotificationSelect = new JComboBox<>(Arrays.stream(FlashNotification.values()).filter(fn -> fn != FlashNotification.DISABLED).toArray(FlashNotification[]::new));
        flashNotificationSelect.setToolTipText("The screen flash mode");
        flashNotificationSelect.setSelectedItem(screenFlash.flashNotification);
        flashNotificationSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            list.setToolTipText(value.toString());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        flashNotificationSelect.addActionListener(e -> {
            screenFlash.flashNotification = flashNotificationSelect.getItemAt(flashNotificationSelect.getSelectedIndex());
            onChangeListener.run();
        });
        this.settings.add(flashNotificationSelect);
    }
}
