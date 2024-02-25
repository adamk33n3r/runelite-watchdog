package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.GameMessageType;
import com.adamk33n3r.runelite.watchdog.PlayerChatType;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.adamk33n3r.runelite.watchdog.ui.Icons;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import javax.swing.JButton;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MessagePickerButton {
    public static JButton createNotificationPickerButton(Consumer<String> callback) {
        return PanelUtils.createActionButton(Icons.PICKER, Icons.PICKER_HOVER, "Pick a recent notification", (btn, mod) -> WatchdogPlugin.getInstance().getPanel().pickNotification(callback));
    }

    public static JButton createGameMessagePickerButton(Consumer<String> callback, Supplier<GameMessageType> typeFilter) {
        return PanelUtils.createActionButton(Icons.PICKER, Icons.PICKER_HOVER, "Pick a message from chat", (btn, mod) -> WatchdogPlugin.getInstance().getPanel().pickMessage(callback, (msg) -> typeFilter.get().isOfType(msg.getType())));
    }

    public static JButton createPlayerChatPickerButton(Consumer<String> callback, Supplier<PlayerChatType> typeFilter) {
        return PanelUtils.createActionButton(Icons.PICKER, Icons.PICKER_HOVER, "Pick a message from chat", (btn, mod) -> WatchdogPlugin.getInstance().getPanel().pickMessage(callback, (msg) -> typeFilter.get().isOfType(msg.getType())));
    }
}
