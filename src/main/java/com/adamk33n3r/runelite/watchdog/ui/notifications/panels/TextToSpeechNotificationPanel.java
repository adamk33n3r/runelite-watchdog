package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.elevenlabs.ElevenLabs;
import com.adamk33n3r.runelite.watchdog.elevenlabs.Voice;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VoiceChooser;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.NotificationsPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.Font;

@Slf4j
public class TextToSpeechNotificationPanel extends NotificationPanel {
    public TextToSpeechNotificationPanel(TextToSpeech notification, NotificationsPanel parentPanel, Runnable onChangeListener, PanelUtils.OnRemove onRemove) {
        super(notification, parentPanel, onChangeListener, onRemove);

        this.rebuild();
    }

    private void rebuild() {
        this.settings.removeAll();

        TextToSpeech notification = (TextToSpeech) this.notification;

        if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
            JLabel ttsLabel = new JLabel("<html>Enable TTS in the config to use this Notification type</html>");
            ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
            this.settings.add(ttsLabel);
            JButton settingsBtn = new JButton("Open Config");
            settingsBtn.addActionListener(ev -> WatchdogPlugin.getInstance().openConfiguration());
            this.settings.add(settingsBtn);
            return;
        }

        FlatTextArea messageTextArea = PanelUtils.createTextField(
            "Enter your message...",
            "The message to play",
            notification.getMessage(),
            val -> {
                notification.setMessage(val);
                onChangeListener.run();
            });
        this.settings.add(messageTextArea);

        JComboBox<TTSSource> sourceSelect = PanelUtils.createSelect(TTSSource.values(), notification.getSource(), (selected) -> {
            notification.setSource(selected);
            onChangeListener.run();
            this.rebuild();
            this.revalidate();
        });
        this.settings.add(sourceSelect);

        switch (notification.getSource()) {
            case ELEVEN_LABS:
                if (WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey().isEmpty()) {
                    JLabel ttsLabel = new JLabel("<html>Add your API key in the config to use Eleven Labs</html>");
                    ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
                    this.settings.add(ttsLabel);
                    JButton settingsBtn = new JButton("Open Config");
                    settingsBtn.addActionListener(ev -> WatchdogPlugin.getInstance().openConfiguration());
                    this.settings.add(settingsBtn);
                    return;
                }
                JComboBox<Voice> voiceSelect = PanelUtils.createSelect(new Voice[]{}, null, Voice::getName, "Loading...", (voice) -> {
                    notification.setElevenLabsVoiceId(voice.getVoiceId());
                    //Not serialized
                    notification.setElevenLabsVoice(voice);
                });

                ElevenLabs.getVoices(WatchdogPlugin.getInstance().getHttpClient(), (voices) -> {
                    SwingUtilities.invokeLater(() -> {
                        // Store the voice id prior to adding to the list because adding the first item will select it
                        String elevenLabsVoiceId = notification.getElevenLabsVoiceId();
                        voices.getVoices().forEach((voice) -> {
                            voiceSelect.addItem(voice);
                            if (elevenLabsVoiceId == null) {
                                if (voice.getName().equals(WatchdogPlugin.getInstance().getConfig().defaultElevenLabsVoice())) {
                                    voiceSelect.setSelectedItem(voice);
                                }
                            } else {
                                if (voice.getVoiceId().equals(elevenLabsVoiceId)) {
                                    voiceSelect.setSelectedItem(voice);
                                }
                            }
                        });
                    });
                }, log::error);
                this.settings.add(voiceSelect);
                break;
            case LEGACY:
                JLabel deprecatedWarning = new JLabel("<html>The Legacy TTS API is deprecated and will likely be removed in the future.</html>");
                this.settings.add(deprecatedWarning);
                JSlider rateSlider = new JSlider(1, 5, notification.getRate());
                rateSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                rateSlider.addChangeListener(ev -> {
                    notification.setRate(rateSlider.getValue());
                    onChangeListener.run();
                });
                this.settings.add(PanelUtils.createIconComponent(Icons.SPEED, "The speed of the generated speech", rateSlider));

                VoiceChooser voiceChooser = new VoiceChooser(notification);
                voiceChooser.addActionListener(e -> onChangeListener.run());
                this.settings.add(PanelUtils.createIconComponent(Icons.SPEECH, "The voice to generate speech with", voiceChooser));
                break;
        }

        VolumeSlider volumeSlider = new VolumeSlider(notification);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> onChangeListener.run());
        this.settings.add(PanelUtils.createIconComponent(Icons.VOLUME, "The volume to playback speech", volumeSlider));
    }
}
