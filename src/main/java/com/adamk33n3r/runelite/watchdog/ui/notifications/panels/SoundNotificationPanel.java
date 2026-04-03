package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.notifications.Sound;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
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

public class SoundNotificationPanel extends NotificationContentPanel<Sound> {

    public SoundNotificationPanel(Sound notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        String[] supportedExtensions = Stream.concat(
            Arrays.stream(AudioSystem.getAudioFileTypes())
                .map(AudioFileFormat.Type::getExtension),
            Stream.of("mp3")
        ).toArray(String[]::new);
        this.add(new JLabel("Choose sound (" + Arrays.stream(supportedExtensions).map(ext -> '.' + ext).collect(Collectors.joining(", ")) + ")"));
        this.add(PanelUtils.createFileChooser(null, "Path to the sound file", ev -> {
            JFileChooser fileChooser = (JFileChooser) ev.getSource();
            this.notification.setPath(fileChooser.getSelectedFile().getAbsolutePath());
            this.onChange.run();
        }, this.notification.getPath(), "Sound Files", supportedExtensions));

        VolumeSlider volumeSlider = new VolumeSlider(this.notification);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> this.onChange.run());
        this.add(PanelUtils.createIconComponent(Icons.VOLUME, "The volume to playback sound", volumeSlider));

        JSpinner repeatDuration = PanelUtils.createSpinner(this.notification.getRepeatDuration(), -1, 120, 1, val -> {
            this.notification.setRepeatDuration(val);
            this.onChange.run();
        });
        this.add(PanelUtils.createIconComponent(Icons.CLOCK, "Duration to repeat sound, use -1 to repeat until cancelled", repeatDuration));
    }
}
