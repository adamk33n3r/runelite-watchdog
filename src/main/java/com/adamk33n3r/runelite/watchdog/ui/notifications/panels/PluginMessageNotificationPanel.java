package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.PluginMessage;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextAreaNamespace;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.ColorScheme;

public class PluginMessageNotificationPanel extends NotificationPanel {
    public PluginMessageNotificationPanel(PluginMessage notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        this.rebuild();
    }
    private void rebuild() {
        this.settings.removeAll();

        PluginMessage notification = (PluginMessage) this.notification;

        FlatTextAreaNamespace fullText = PanelUtils.createTextFieldNamespace(
            "Namespace",
            "The namespace of the plugin message. Usually the name of the plugin.",
            notification.getNamespace(),
            "Method",
            "The method or action of the plugin message.",
            notification.getName(),
            (val1, val2) -> {
                notification.setNamespace(val1);
                notification.setName(val2);
                onChangeListener.run();
            }
        );
        fullText.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.settings.add(fullText);

        FlatTextArea dataText = PanelUtils.createTextField(
            "Data (JSON)",
            "The data of the plugin message as a JSON object string. Leave empty if not needed.",
            notification.getData(),
            val -> {
                notification.setData(val);
                onChangeListener.run();
            }
        );
        this.settings.add(dataText);
    }
}
