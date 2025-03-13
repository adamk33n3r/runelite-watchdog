package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.notifications.Dink;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.config.ConfigManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class DinkNotificationPanel extends NotificationPanel {
    private final ConfigManager configManager;

    public DinkNotificationPanel(Dink notification, NotificationsPanel parentPanel, ConfigManager configManager, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);
        this.configManager = configManager;

        this.rebuild();
    }

    private void rebuild() {
        this.settings.removeAll();

        Dink notification = (Dink) this.notification;

        String installedPlugins = configManager.getConfiguration("runelite", "externalPlugins");
        if (!installedPlugins.contains("dink")) {
            JLabel installDinkLabel = new JLabel("<html>Install the Dink plugin to use this Notification type</html>");
            installDinkLabel.setFont(new Font(installDinkLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, installDinkLabel.getFont().getSize()));
            this.settings.add(installDinkLabel);
            return;
        }

        boolean externalEnabled = configManager.getConfiguration("dinkplugin", "notifyExternal").equals("true");
        if (!externalEnabled) {
            JLabel enableExternalLabel = new JLabel("<html>Enable External Plugin Notifications in Dink's config to use this Notification type</html>");
            enableExternalLabel.setFont(new Font(enableExternalLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, enableExternalLabel.getFont().getSize()));
            this.settings.add(enableExternalLabel);
            return;
        }

        FlatTextArea flatTextArea = new FlatTextArea("Enter your message...", true);
        flatTextArea.setText(notification.getMessage());
        ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(4096));
        flatTextArea.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
            notification.setMessage(flatTextArea.getText());
        });
        flatTextArea.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                flatTextArea.getTextArea().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                onChangeListener.run();
            }
        });
        this.settings.add(flatTextArea);

        JCheckBox includeScreenshot = PanelUtils.createCheckbox("Include Screenshot", "Whether or not to include a screenshot in the discord message", notification.isIncludeScreenshot(), (selected) -> {
            notification.setIncludeScreenshot(selected);
            onChangeListener.run();
        });
        this.settings.add(includeScreenshot);
    }
}
