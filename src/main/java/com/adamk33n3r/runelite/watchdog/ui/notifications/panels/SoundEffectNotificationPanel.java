package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogProperties;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JSpinner;

public class SoundEffectNotificationPanel extends NotificationPanel {

    public SoundEffectNotificationPanel(SoundEffect soundEffect, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(soundEffect, parentPanel, onChangeListener, onRemove);

        JRichTextPane richTextPane = new JRichTextPane();
        richTextPane.setContentType("text/html");
        richTextPane.setText("<html>Go to <a href='" + WatchdogProperties.getProperties().getProperty("watchdog.wikiPage.soundIDs") + "'>this wiki page</a> to get a list<br>of sound ids</html>");
        this.settings.add(richTextPane);
        JSpinner soundID = PanelUtils.createSpinner(soundEffect.getSoundID(), 0, 99999, 1, (val) -> {
            soundEffect.setSoundID(val);
            onChangeListener.run();
        });
        this.settings.add(soundID);

        VolumeSlider volumeSlider = new VolumeSlider(soundEffect);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> onChangeListener.run());
        this.settings.add(PanelUtils.createIconComponent(
            Icons.VOLUME,
            "The volume to playback sound effect (if muted in game, otherwise will use game setting)",
            volumeSlider
        ));
    }
}
