package com.adamk33n3r.runelite.narration;

import net.runelite.client.config.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("narration")
public interface NarrationConfig extends Config
{
	@ConfigSection(
		name = "Narration Settings",
		description = "General settings",
		position = 0
	)
	String narrationSettingsSection = "settings";
	@ConfigSection(
		name = "Narration Triggers",
		description = "Triggers for narrating different things",
		position = 1
	)
	String narrationTriggersSection = "triggers";
	@ConfigSection(
		name = "Narrator Settings",
		description = "Settings for how the narrator sounds",
		position = 2
	)
	String narrationVoiceSection = "voice";


	@ConfigItem(
		keyName = "saySpeakerNames",
		name = "Say Speaker Names",
		description = "Say the speakers name before their dialog",
		section = narrationSettingsSection,
		position = 0
	)
	default boolean saySpeakerName() { return true; }
	@ConfigItem(
		keyName = "usePlayerName",
		name = "Use Player Name",
		description = "Say your name during dialog instead of 'You'",
		section = narrationSettingsSection,
		position = 1
	)
	default boolean usePlayerName() { return false; }
	@ConfigItem(
		keyName = "queueDialog",
		name = "Queue Dialog",
		description = "Enable this to queue up dialog",
		section = narrationSettingsSection,
		position = 2
	)
	default boolean enableQueueDialog() { return false; }
	@ConfigItem(
		keyName = "narrateHotkey",
		name = "Narrate Hotkey",
		description = "The hotkey that triggers narration for what you're hovering over",
		section = narrationSettingsSection,
		position = 3
	)
	default Keybind narrateHotkey() { return new Keybind(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK); }
	@ConfigItem(
		keyName = "narrateQuantityHotkey",
		name = "Narrate Quantity Hotkey",
		description = "The hotkey that narrates the quantity of the hovered item",
		section = narrationSettingsSection,
		position = 3
	)
	default Keybind narrateQuantityHotkey() { return new Keybind(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK); }



	@ConfigItem(
		keyName = "enableDialog",
		name = "Dialog",
		description = "Enables narration of dialog",
		section = narrationTriggersSection,
		position = 0
	)
	default boolean enableDialog() { return true; }
	@ConfigItem(
		keyName = "enableOnClick",
		name = "On Click",
		description = "Enables narrating on click",
		section = narrationTriggersSection,
		position = 1
	)
	default boolean enableOnClick() { return true; }
	@ConfigItem(
		keyName = "enableOnMenu",
		name = "On Menu (Warning at banks)",
		description = "Enables narrating on menu open (right click)",
		section = narrationTriggersSection,
		position = 2
	)
	default boolean enableOnMenu() { return false; }
	@ConfigItem(
		keyName = "enableOnExamine",
		name = "On Examine",
		description = "Enables narrating on examine",
		section = narrationTriggersSection,
		position = 3
	)
	default boolean enableOnExamine() { return true; }




	@Range(
		min = 0,
		max = 400
	)
	@ConfigItem(
		keyName = "wpm",
		name = "WPM",
		description = "Words per minute of the narrator",
		section = narrationVoiceSection,
		hidden = false
	)
	default int wpm() { return 150; }
	@Range(
		min = 50,
		max = 200
	)
	@Units("hz")
	@ConfigItem(
		keyName = "pitch",
		name = "Pitch",
		description = "Pitch of the narrator",
		section = narrationVoiceSection,
		hidden = false
	)
	default int pitch() { return 100; }
	@Range(
		min = 0,
		max = 10
	)
	@ConfigItem(
		keyName = "volume",
		name = "Volume",
		description = "Volume of the narrator",
		section = narrationVoiceSection,
		hidden = false
	)
	default int volume() { return 8; }

}
