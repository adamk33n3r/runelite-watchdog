package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Sound;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.ui.ColorScheme;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoundNotificationPanel extends NotificationPanel {

    public SoundNotificationPanel(Sound sound, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(sound, parentPanel, onChangeListener, onRemove);

        String[] supportedExtensions = Stream.concat(
            Arrays.stream(AudioSystem.getAudioFileTypes())
                .map(AudioFileFormat.Type::getExtension),
            Stream.of("mp3")
        ).toArray(String[]::new);
        this.settings.add(new JLabel("Supports " + Arrays.stream(supportedExtensions).map(ext -> '.' + ext).collect(Collectors.joining(", "))));
        this.settings.add(PanelUtils.createFileChooser(null, "Path to the sound file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            sound.setPath(fileChooser.getSelectedFile().getAbsolutePath());
            onChangeListener.run();
        }, sound.getPath(), "Sound Files", supportedExtensions));

        VolumeSlider volumeSlider = new VolumeSlider(sound);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> onChangeListener.run());
        this.settings.add(PanelUtils.createIconComponent(Icons.VOLUME, "The volume to playback sound", volumeSlider));

        JSpinner repeatDuration = PanelUtils.createSpinner(sound.getRepeatDuration(), -1, 120, 1, val -> {
            sound.setRepeatDuration(val);
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(Icons.CLOCK, "Duration to repeat sound, use -1 to repeat until cancelled", repeatDuration));
    }
}
