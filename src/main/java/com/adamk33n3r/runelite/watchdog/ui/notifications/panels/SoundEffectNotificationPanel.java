package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogProperties;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JSpinner;

public class SoundEffectNotificationPanel extends NotificationContentPanel<SoundEffect> {

    public SoundEffectNotificationPanel(SoundEffect notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText("<html>Go to <a href='" + WatchdogProperties.getProperties().getProperty("watchdog.wikiPage.soundIDs") +
            "'>this wiki page</a> to get a list<br>of sound ids.</html>");
        this.add(richTextPane);

        JSpinner soundID = PanelUtils.createSpinner(this.notification.getSoundID(), 0, 99999, 1, val -> {
            this.notification.setSoundID(val);
            this.onChange.run();
        });
        this.add(soundID);

        VolumeSlider volumeSlider = new VolumeSlider(this.notification);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> this.onChange.run());
        this.add(PanelUtils.createIconComponent(Icons.VOLUME, "The volume to playback sound effect", volumeSlider));
    }
}
