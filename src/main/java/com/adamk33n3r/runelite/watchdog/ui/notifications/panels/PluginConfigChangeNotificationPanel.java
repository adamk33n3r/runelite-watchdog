package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.PluginConfigChange;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextAreaNamespace;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class PluginConfigChangeNotificationPanel extends NotificationPanel {
    public PluginConfigChangeNotificationPanel(PluginConfigChange notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        this.rebuild();
    }

    private void rebuild() {
        this.settings.removeAll();

        PluginConfigChange notification = (PluginConfigChange) this.notification;

        JLabel warningLabel = new JLabel("<html>WARNING: This notification could potentially mess up your config profile if you are not careful! Make sure you are setting the correct config key and that the value is the type the plugin expects. Make a backup of your profile just in case something goes wrong.</html>");
        warningLabel.setFont(new Font(warningLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, warningLabel.getFont().getSize()));
        this.settings.add(warningLabel);

        FlatTextAreaNamespace fullText = PanelUtils.createTextFieldNamespace(
            "Plugin",
            "The group name. Usually the name of the plugin.",
            notification.getPluginName(),
            ".",
            "Key",
            "The config key of the setting to change.",
            notification.getConfigKey(),
            (val1, val2) -> {
                notification.setPluginName(val1);
                notification.setConfigKey(val2);
                onChangeListener.run();
            }
        );
        fullText.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        this.settings.add(fullText);

        FlatTextArea dataText = PanelUtils.createTextArea(
            "Data",
            "The data to set the config key to. Check the profile file for the correct format.",
            notification.getData(),
            val -> {
                notification.setData(val);
                onChangeListener.run();
            }
        );
        this.settings.add(dataText);
    }
}
