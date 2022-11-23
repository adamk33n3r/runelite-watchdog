package com.adamk33n3r.runelite.watchdog.ui.notifications.panels;

import com.adamk33n3r.runelite.watchdog.LengthLimitFilter;
import com.adamk33n3r.runelite.watchdog.SimpleDocumentListener;
import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.ui.FlatTextArea;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VoiceChooser;
import com.adamk33n3r.runelite.watchdog.ui.notifications.VolumeSlider;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

public class TextToSpeechNotificationPanel extends NotificationPanel {
    public TextToSpeechNotificationPanel(TextToSpeech notification) {
        super(notification);

        FlatTextArea flatTextArea = new FlatTextArea(true);
        flatTextArea.setText(notification.getMessage());
        ((AbstractDocument) flatTextArea.getDocument()).setDocumentFilter(new LengthLimitFilter(200));
        flatTextArea.getDocument().addDocumentListener((SimpleDocumentListener) ev -> {
            notification.setMessage(flatTextArea.getText());
        });
        this.container.add(flatTextArea);



        JSlider rateSlider = new JSlider(1, 5, notification.getRate());
        rateSlider.addChangeListener(ev -> {
            notification.setRate(rateSlider.getValue());
        });
        // Change icon to something indicating speed....
        this.container.add(PanelUtils.createIconComponent(VOLUME_ICON, "The speed of the generated speech", rateSlider));
//        this.container.add(rateSlider);

        // Should be an icon of a head looking right with the same "sound waves" of the volume icon
        // "speech" icon
        this.container.add(PanelUtils.createIconComponent(VOLUME_ICON, "The voice to generate speech with", new VoiceChooser(notification)));
//        this.container.add(new VoiceChooser(notification));

        this.container.add(PanelUtils.createIconComponent(VOLUME_ICON, "The volume to playback speech", new VolumeSlider(notification)));
    }
}
