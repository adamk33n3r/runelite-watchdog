package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.notifications.Dink;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.config.ConfigManager;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.text.AbstractDocument;
import java.awt.Font;

public class DinkNotificationPanel extends NotificationContentPanel<Dink> {
    private final ConfigManager configManager;

    public DinkNotificationPanel(Dink notification, ConfigManager configManager, Runnable onChange) {
        super(notification, onChange);
        this.configManager = configManager;
        this.init();
    }

    @Override
    protected void buildContent() {
        String installedPlugins = this.configManager.getConfiguration("runelite", "externalPlugins");
        if (!installedPlugins.contains("dink")) {
            JLabel installDinkLabel = new JLabel("<html>Install the Dink plugin to use this Notification type</html>");
            installDinkLabel.setFont(new Font(installDinkLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, installDinkLabel.getFont().getSize()));
            this.add(installDinkLabel);
            return;
        }

        boolean externalEnabled = this.configManager.getConfiguration("dinkplugin", "notifyExternal").equals("true");
        if (!externalEnabled) {
            JLabel enableExternalLabel = new JLabel("<html>Enable External Plugin Notifications in Dink's config to use this Notification type</html>");
            enableExternalLabel.setFont(new Font(enableExternalLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, enableExternalLabel.getFont().getSize()));
            this.add(enableExternalLabel);
            return;
        }

        var message = PanelUtils.createTextField(
            "Enter your message...",
            "Message to send with Dink",
            this.notification.getMessage(),
            val -> {
                this.notification.setMessage(val);
                this.onChange.run();
            }
        );
        ((AbstractDocument) message.getDocument()).setDocumentFilter(new LengthLimitFilter(4096));
        this.add(message);

        var urls = PanelUtils.createTextField(
            "Custom urls (optional)...",
            "Semicolon separated list of urls to send with Dink",
            this.notification.getUrls(),
            val -> {
                this.notification.setUrls(val);
                if (val == null || val.isEmpty()) {
                    this.notification.setUrls(null);
                }
                this.onChange.run();
            }
        );
        ((AbstractDocument) urls.getDocument()).setDocumentFilter(new LengthLimitFilter(4096));
        this.add(urls);

        JCheckBox includeScreenshot = PanelUtils.createCheckbox("Include Screenshot", "Whether or not to include a screenshot in the discord message", this.notification.isIncludeScreenshot(), selected -> {
            this.notification.setIncludeScreenshot(selected);
            this.onChange.run();
        });
        this.add(includeScreenshot);
    }
}
