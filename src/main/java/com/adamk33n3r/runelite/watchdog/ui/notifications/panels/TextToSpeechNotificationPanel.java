package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VoiceChooser;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

public class TextToSpeechNotificationPanel extends NotificationPanel {
    private static final ImageIcon SPEECH_ICON;
    private static final ImageIcon SPEED_ICON;

    static {
        final BufferedImage speechImg = ImageUtil.loadImageResource(NotificationPanel.class, "speech_icon.png");
        final BufferedImage speedImg = ImageUtil.loadImageResource(NotificationPanel.class, "speed_icon.png");

        SPEECH_ICON = new ImageIcon(ImageUtil.luminanceOffset(speechImg, -80));
        SPEED_ICON = new ImageIcon(ImageUtil.luminanceOffset(speedImg, -80));
    }

    public TextToSpeechNotificationPanel(TextToSpeech notification) {
        super(notification);

        if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
            JLabel ttsLabel = new JLabel("<html>Enable TTS in the config to use this Notification type</html>");
            ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
            this.container.add(ttsLabel);
            return;
        }

        FlatTextArea flatTextArea = new FlatTextArea(true);
        flatTextArea.setText(notification.getMessage());
        ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
        flatTextArea.getDocument().addDocumentListener((SimpleDocumentListener) ev -> notification.setMessage(flatTextArea.getText()));
        flatTextArea.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                flatTextArea.getTextArea().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        this.container.add(flatTextArea);


        JSlider rateSlider = new JSlider(1, 5, notification.getRate());
        rateSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
//        rateSlider.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        rateSlider.addChangeListener(ev -> notification.setRate(rateSlider.getValue()));
        this.container.add(PanelUtils.createIconComponent(SPEED_ICON, "The speed of the generated speech", rateSlider));
//        this.container.add(rateSlider);

        // Should be an icon of a head looking right with the same "sound waves" of the volume icon
        // "speech" icon
        this.container.add(PanelUtils.createIconComponent(SPEECH_ICON, "The voice to generate speech with", new VoiceChooser(notification)));
//        this.container.add(new VoiceChooser(notification));

        VolumeSlider volumeSlider = new VolumeSlider(notification);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        this.container.add(PanelUtils.createIconComponent(VOLUME_ICON, "The volume to playback sound", volumeSlider));
    }
}
