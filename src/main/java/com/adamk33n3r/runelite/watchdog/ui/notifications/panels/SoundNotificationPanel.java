package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Sound;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import java.util.Arrays;

public class SoundNotificationPanel extends NotificationPanel {

    public SoundNotificationPanel(Sound sound, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
        super(sound, onChangeListener, onRemove);

        String[] supportedExtensions = Arrays.stream(AudioSystem.getAudioFileTypes()).map(AudioFileFormat.Type::getExtension).toArray(String[]::new);
        this.settings.add(new JLabel("Supports " + String.join(", ", Arrays.stream(supportedExtensions).map(ext -> "."+ext).toArray(String[]::new))));
        this.settings.add(PanelUtils.createFileChooser(null, "Path to the sound file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            sound.setPath(fileChooser.getSelectedFile().getAbsolutePath());
            onChangeListener.run();
        }, sound.getPath(), "Sound Files", supportedExtensions));

        VolumeSlider volumeSlider = new VolumeSlider(sound);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        this.settings.add(PanelUtils.createIconComponent(VOLUME_ICON, "The volume to playback speech", volumeSlider));
    }
}
