package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogProperties;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JPanel;
import javax.swing.JSpinner;

public class SoundEffectNotificationPanel extends NotificationPanel {

    public SoundEffectNotificationPanel(SoundEffect soundEffect, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(soundEffect, parentPanel, onChangeListener, onRemove);
        this.buildContent(this.settings, onChangeListener);
    }

    @Override
    protected void buildContent(JPanel container, Runnable onChange) {
        buildContent((SoundEffect) this.notification, container, onChange);
    }

    public static void buildContent(SoundEffect soundEffect, JPanel container, Runnable onChange) {
        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText("<html>Go to <a href='" + WatchdogProperties.getProperties().getProperty("watchdog.wikiPage.soundIDs") +
            "'>this wiki page</a> to get a list<br>of sound ids.</html>");
        container.add(richTextPane);
        JSpinner soundID = PanelUtils.createSpinner(soundEffect.getSoundID(), 0, 99999, 1, (val) -> {
            soundEffect.setSoundID(val);
            onChange.run();
        });
        container.add(soundID);

        VolumeSlider volumeSlider = new VolumeSlider(soundEffect);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> onChange.run());
        container.add(PanelUtils.createIconComponent(
            Icons.VOLUME,
            "The volume to playback sound effect",
            volumeSlider
        ));
    }
}
