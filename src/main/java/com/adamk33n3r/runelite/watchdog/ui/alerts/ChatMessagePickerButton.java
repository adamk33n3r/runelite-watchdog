package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.*;
import java.util.function.Consumer;

public class ChatMessagePickerButton extends JPanel {
    public ChatMessagePickerButton(Consumer<String> callback) {
        JButton picker = PanelUtils.createActionButton(Icons.PICKER, Icons.PICKER_HOVER, "Pick a message from chat", (btn, mod) -> WatchdogPlugin.getInstance().getPanel().pickMessage(callback));
        this.add(picker);
    }
}
