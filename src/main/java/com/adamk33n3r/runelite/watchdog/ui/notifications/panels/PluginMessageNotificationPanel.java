package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.PluginMessage;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;

public class PluginMessageNotificationPanel extends NotificationContentPanel<PluginMessage> {

    public PluginMessageNotificationPanel(PluginMessage notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        var fullText = PanelUtils.createTextFieldNamespace(
            "Namespace",
            "The namespace of the plugin message. Usually the name of the plugin.",
            this.notification.getNamespace(),
            ":",
            "Method",
            "The method or action of the plugin message.",
            this.notification.getName(),
            (val1, val2) -> {
                this.notification.setNamespace(val1);
                this.notification.setName(val2);
                this.onChange.run();
            }
        );
        fullText.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.add(fullText);

        this.add(PanelUtils.createTextField(
            "Data (JSON)",
            "The data of the plugin message as a JSON object string. Leave empty if not needed.",
            this.notification.getData(),
            val -> {
                this.notification.setData(val);
                this.onChange.run();
            }
        ));
    }
}
