package com.adamk33n3r.runelite.watchdog.ui.nodegraph;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.alerts.FlashMode;
import com.adamk33n3r.runelite.watchdog.elevenlabs.ElevenLabs;
import com.adamk33n3r.runelite.watchdog.elevenlabs.Voice;
import com.adamk33n3r.runelite.watchdog.notifications.Notification;
import com.adamk33n3r.runelite.watchdog.notifications.ScreenFlash;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.tts.TTSSource;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VoiceChooser;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class NotificationNode extends AcceptsConnectionNode {
    public NotificationNode(Graph graph, int x, int y, String name, Color color, Notification notification, ColorPickerManager colorPickerManager) {
        super(graph, x, y, name, color);

        ConnectionPoint inputConnectionPoint = new ConnectionPoint(this);
        this.add(inputConnectionPoint, BorderLayout.WEST);

        JButton testBtn = new JButton("TEST");
        testBtn.addActionListener((ev) -> notification.fireForced(new String[]{}));
        this.items.add(testBtn);

        if (notification instanceof ScreenFlash) {
            ScreenFlash screenFlash = (ScreenFlash) notification;
            ColorJButton colorPickerBtn = PanelUtils.createColorPicker(
                "Pick a color",
                "The color to flash the screen",
                "Flash Color",
                this,
                screenFlash.getColor(),
                colorPickerManager,
                true,
                val -> {
                    screenFlash.setColor(val);
//                    onChangeListener.run();
                });
            this.items.add(colorPickerBtn);


            JComboBox<FlashMode> flashModeSelect = new JComboBox<>(FlashMode.values());
            flashModeSelect.setToolTipText("The screen flash mode");
            flashModeSelect.setSelectedItem(screenFlash.getFlashMode());
            flashModeSelect.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                list.setToolTipText(value.getTooltip());
                return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            });
            flashModeSelect.addActionListener(e -> {
                screenFlash.setFlashMode(flashModeSelect.getItemAt(flashModeSelect.getSelectedIndex()));
//                onChangeListener.run();
            });
            this.items.add(flashModeSelect);

            JSpinner flashDuration = PanelUtils.createSpinner(screenFlash.getFlashDuration(), 0, 120, 1, val -> {
                screenFlash.setFlashDuration(val);
//                onChangeListener.run();
            });
            this.items.add(PanelUtils.createIconComponent(Icons.CLOCK, "Duration of flash, use 0 to flash until cancelled", flashDuration));
        } else if (notification instanceof TextToSpeech) {
            TextToSpeech tts = (TextToSpeech) notification;

//            if (!WatchdogPlugin.getInstance().getConfig().ttsEnabled()) {
//                JLabel ttsLabel = new JLabel("<html>Enable TTS in the config to use this Notification type</html>");
//                ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
//                this.items.add(ttsLabel);
//                JButton settingsBtn = new JButton("Open Config");
//                settingsBtn.addActionListener(ev -> WatchdogPlugin.getInstance().openConfiguration());
//                this.items.add(settingsBtn);
//                return;
//            }

            FlatTextArea flatTextArea = new FlatTextArea("Enter your message...", true);
            flatTextArea.setText(tts.getMessage());
            ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
            flatTextArea.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
                tts.setMessage(flatTextArea.getText());
            });
            flatTextArea.getTextArea().addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    flatTextArea.getTextArea().selectAll();
                }

                @Override
                public void focusLost(FocusEvent e) {
//                    onChangeListener.run();
                }
            });
            this.items.add(flatTextArea);

            JComboBox<TTSSource> sourceSelect = PanelUtils.createSelect(TTSSource.values(), tts.getSource(), (selected) -> {
                tts.setSource(selected);
//                onChangeListener.run();
//                this.rebuild();
//                this.revalidate();
            });
            this.items.add(sourceSelect);

            switch (tts.getSource()) {
                case ELEVEN_LABS:
                    if (WatchdogPlugin.getInstance() != null && WatchdogPlugin.getInstance().getConfig().elevenLabsAPIKey().isEmpty()) {
                        JLabel ttsLabel = new JLabel("<html>Add your API key in the config to use Eleven Labs</html>");
                        ttsLabel.setFont(new Font(ttsLabel.getFont().getFontName(), Font.ITALIC | Font.BOLD, ttsLabel.getFont().getSize()));
                        this.items.add(ttsLabel);
                        JButton settingsBtn = new JButton("Open Config");
                        settingsBtn.addActionListener(ev -> WatchdogPlugin.getInstance().openConfiguration());
                        this.items.add(settingsBtn);
                        return;
                    }
                    JComboBox<Voice> voiceSelect = PanelUtils.createSelect(new Voice[]{}, null, Voice::getName, (voice) -> {
                        tts.setElevenLabsVoiceId(voice.getVoiceId());
                        //Not serialized
                        tts.setElevenLabsVoice(voice);
                    });

                    ElevenLabs.getVoices(WatchdogPlugin.getInstance().getHttpClient(), (voices) -> {
                        SwingUtilities.invokeLater(() -> {
                            // Store the voice id prior to adding to the list because adding the first item will select it
                            String elevenLabsVoiceId = tts.getElevenLabsVoiceId();
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
                    });
                    this.items.add(voiceSelect);
                    break;
                case LEGACY:
                    JSlider rateSlider = new JSlider(1, 5, tts.getRate());
                    rateSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
                    rateSlider.addChangeListener(ev -> {
                        tts.setRate(rateSlider.getValue());
//                        onChangeListener.run();
                    });
                    this.items.add(PanelUtils.createIconComponent(Icons.SPEED, "The speed of the generated speech", rateSlider));

                    VoiceChooser voiceChooser = new VoiceChooser(tts);
//                    voiceChooser.addActionListener(e -> onChangeListener.run());
                    this.items.add(PanelUtils.createIconComponent(Icons.SPEECH, "The voice to generate speech with", voiceChooser));
                    break;
            }

            VolumeSlider volumeSlider = new VolumeSlider(tts);
            volumeSlider.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);
//            volumeSlider.addChangeListener(e -> onChangeListener.run());
            this.items.add(PanelUtils.createIconComponent(Icons.VOLUME, "The volume to playback speech", volumeSlider));
        }
    }
}
