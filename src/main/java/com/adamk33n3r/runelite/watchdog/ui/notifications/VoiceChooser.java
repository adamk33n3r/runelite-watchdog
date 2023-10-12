package com.adamk33n3r.runelite.watchdog.ui.notifications;

import com.adamk33n3r.runelite.watchdog.notifications.TextToSpeech;
import com.adamk33n3r.runelite.watchdog.notifications.tts.Voice;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;

public class VoiceChooser extends JComboBox<Voice> {
    public VoiceChooser(TextToSpeech notification) {
        super(Voice.values());
        this.setSelectedItem(notification.getLegacyVoice());
        this.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            list.setToolTipText(value.toString());
            return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        });
        this.addActionListener(ev -> notification.setLegacyVoice(this.getItemAt(this.getSelectedIndex())));
    }
}
