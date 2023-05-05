package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.SoundEffectIDWrapper;
import com.adamk33n3r.runelite.watchdog.WatchdogProperties;
import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.notifications.SoundEffect;
import com.adamk33n3r.runelite.watchdog.ui.AutoCompleteComboBox;
import com.adamk33n3r.runelite.watchdog.ui.AutoCompletion;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.info.JRichTextPane;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class SoundEffectNotificationPanel extends NotificationPanel {

    public SoundEffectNotificationPanel(SoundEffect soundEffect, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
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

        SoundEffectIDWrapper.SoundEffect[] soundEffects = SoundEffectIDWrapper.getSoundEffects().toArray(new SoundEffectIDWrapper.SoundEffect[0]);
        JComboBox<SoundEffectIDWrapper.SoundEffect> flashModeSelect = new JComboBox<>(soundEffects);
        flashModeSelect.setToolTipText("The sound effect");
        flashModeSelect.setSelectedItem(soundEffect.getSoundEffect());
        flashModeSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            list.setToolTipText(value.getName());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        flashModeSelect.addActionListener(e -> {
            soundEffect.setSoundEffect(flashModeSelect.getItemAt(flashModeSelect.getSelectedIndex()));
            onChangeListener.run();
        });
        AutoCompletion.enable(flashModeSelect);
        this.settings.add(flashModeSelect);
        AutoCompleteComboBox<SoundEffectIDWrapper.SoundEffect> soundEffectSelect = new AutoCompleteComboBox<>(soundEffects);
        this.settings.add(soundEffectSelect);

        VolumeSlider volumeSlider = new VolumeSlider(soundEffect);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> onChangeListener.run());
        this.settings.add(PanelUtils.createIconComponent(VOLUME_ICON, "The volume to playback sound effect", volumeSlider));
    }
}
