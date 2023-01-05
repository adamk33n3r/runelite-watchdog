package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.PlaceholderTextField;
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

    public TextToSpeechNotificationPanel(TextToSpeech notification, Runnable onChangeListener, PanelUtils.ButtonClickListener onRemove) {
        super(notification, onChangeListener, onRemove);

        if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
            JLabel ttsLabel = new JLabel("<html>Enable TTS in the config to use this Notification type</html>");
            ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
            this.settings.add(ttsLabel);
            return;
        }

        FlatTextArea flatTextArea = new FlatTextArea("Enter your message...", true);
        flatTextArea.setText(notification.getMessage());
        ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
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


        JSlider rateSlider = new JSlider(1, 5, notification.getRate());
        rateSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        rateSlider.addChangeListener(ev -> {
            notification.setRate(rateSlider.getValue());
            onChangeListener.run();
        });
        this.settings.add(PanelUtils.createIconComponent(SPEED_ICON, "The speed of the generated speech", rateSlider));

        // Should be an icon of a head looking right with the same "sound waves" of the volume icon
        // "speech" icon
        VoiceChooser voiceChooser = new VoiceChooser(notification);
        this.settings.add(PanelUtils.createIconComponent(SPEECH_ICON, "The voice to generate speech with", voiceChooser));
        voiceChooser.addActionListener(e -> onChangeListener.run());

        VolumeSlider volumeSlider = new VolumeSlider(notification);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> onChangeListener.run());
        this.settings.add(PanelUtils.createIconComponent(VOLUME_ICON, "The volume to playback sound", volumeSlider));
    }
}
