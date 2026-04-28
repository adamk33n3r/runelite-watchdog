package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.elevenlabs.ElevenLabs;
import com.adamk33n3r.runelite.watchdog.elevenlabs.Voice;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VoiceChooser;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import java.awt.Font;

@Slf4j
public class TextToSpeechNotificationPanel extends NotificationContentPanel<TextToSpeech> {

    public TextToSpeechNotificationPanel(TextToSpeech notification, Runnable onChange) {
        super(notification, onChange);
        this.init();
    }

    @Override
    protected void buildContent() {
        if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
            JLabel ttsLabel = new JLabel("<html>Enable TTS in the config to use this Notification type</html>");
            ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
            this.add(ttsLabel);
            JButton settingsBtn = new JButton("Open Config");
            settingsBtn.addActionListener(ev -> WatchdogPlugin.getInstance().openConfiguration());
            this.add(settingsBtn);
            return;
        }

        FlatTextArea messageField = PanelUtils.createTextField(
            "Enter your message...",
            "The message to play",
            this.notification.getMessage(),
            val -> {
                this.notification.setMessage(val);
                this.onChange.run();
            });
        JButton resetCacheButton = PanelUtils.createActionButton(
            Icons.REFRESH,
            Icons.REFRESH_HOVER,
            "Reset cached audio for this message. It will regenerate the next time this notification fires",
            (btn, modifiers) -> {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "Delete the cached audio for this message? It will regenerate the next time this notification fires.",
                    "Reset TTS Cache?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    this.notification.clearCache();
                }
            });
        resetCacheButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.add(PanelUtils.createInputGroupWithSuffix(messageField, resetCacheButton));

        JComboBox<TTSSource> sourceSelect = PanelUtils.createSelect(TTSSource.values(), this.notification.getSource(), selected -> {
            this.notification.setSource(selected);
            this.onChange.run();
            this.rebuild();
        });
        this.add(sourceSelect);

        switch (this.notification.getSource()) {
            case ELEVEN_LABS:
                if (WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey().isEmpty()) {
                    JLabel ttsLabel = new JLabel("<html>Add your API key in the config to use Eleven Labs</html>");
                    ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
                    this.add(ttsLabel);
                    JButton settingsBtn = new JButton("Open Config");
                    settingsBtn.addActionListener(ev -> WatchdogPlugin.getInstance().openConfiguration());
                    this.add(settingsBtn);
                    return;
                }
                JComboBox<Voice> voiceSelect = PanelUtils.createSelect(new Voice[]{}, null, Voice::getName, "Loading...", voice -> {
                    this.notification.setElevenLabsVoiceId(voice.getVoiceId());
                    this.notification.setElevenLabsVoice(voice);
                });

                ElevenLabs.getVoices(WatchdogPlugin.getInstance().getHttpClient(), voices -> {
                    SwingUtilities.invokeLater(() -> {
                        String elevenLabsVoiceId = this.notification.getElevenLabsVoiceId();
                        voices.getVoices().forEach(voice -> {
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
                this.add(voiceSelect);
                break;
            case LEGACY:
                JLabel deprecatedWarning = new JLabel("<html>The Legacy TTS API is deprecated and will likely be removed in the future.</html>");
                this.add(deprecatedWarning);
                JSlider rateSlider = new JSlider(1, 5, this.notification.getRate());
                rateSlider.setSnapToTicks(true);
                rateSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                rateSlider.addChangeListener(ev -> {
                    this.notification.setRate(rateSlider.getValue());
                    this.onChange.run();
                });
                this.add(PanelUtils.createIconComponent(Icons.SPEED, "The speed of the generated speech", rateSlider));

                VoiceChooser voiceChooser = new VoiceChooser(this.notification);
                voiceChooser.addActionListener(e -> this.onChange.run());
                this.add(PanelUtils.createIconComponent(Icons.SPEECH, "The voice to generate speech with", voiceChooser));
                break;
        }

        VolumeSlider volumeSlider = new VolumeSlider(this.notification);
        volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
        volumeSlider.addChangeListener(e -> this.onChange.run());
        this.add(PanelUtils.createIconComponent(Icons.VOLUME, "The volume to playback speech", volumeSlider));
    }
}
