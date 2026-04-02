package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Overhead;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.JPanel;
import javax.swing.JSpinner;

public class OverheadNotificationPanel extends MessageNotificationPanel {
    private final ColorPickerManager colorPickerManager;

    public OverheadNotificationPanel(Overhead notification, NotificationsPanel parentPanel, ColorPickerManager colorPickerManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, true, parentPanel, onChangeListener, onRemove);
        this.colorPickerManager = colorPickerManager;
        // MessageNotificationPanel already added the text area; add Overhead-specific controls
        buildExtraContent(notification, this.settings, onChangeListener, colorPickerManager);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((Overhead) this.notification, container, onChange, this.colorPickerManager);
    }

    public static void buildContent(Overhead notification, JPanel container, Runnable onChange, ColorPickerManager colorPickerManager) {
        MessageNotificationPanel.buildContent(notification, true, container, onChange);
        buildExtraContent(notification, container, onChange, colorPickerManager);
    }

    private static void buildExtraContent(Overhead notification, JPanel container, Runnable onChange, ColorPickerManager colorPickerManager) {
        ColorJButton colorPickerBtn = PanelUtils.createColorPicker(
            "Pick a color",
            "The color of the overhead text. Right click to reset.",
            "Text Color",
            container,
            notification.getTextColor(),
            colorPickerManager,
            false,
            val -> {
                notification.setTextColor(val);
                onChange.run();
            });
        container.add(colorPickerBtn);

        JSpinner displayTime = PanelUtils.createSpinner(notification.getDisplayTime(), 1, 99, 1, val -> {
            notification.setDisplayTime(val);
            onChange.run();
        });
        container.add(PanelUtils.createIconComponent(Icons.CLOCK, "Time to display overhead in seconds", displayTime));
    }
}
