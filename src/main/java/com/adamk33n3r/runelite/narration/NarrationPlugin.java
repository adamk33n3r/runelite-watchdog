package com.adamk33n3r.runelite.narration;

import com.adamk33n3r.runelite.narration.tts.*;
import com.google.inject.Provides;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
    name = "Narration"
)
public class NarrationPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private NarrationConfig config;

    @Inject
    private TTSSegmentProcessor ttsSegmentProcessor;

    @Inject
    private KeyManager keyManager;
    @Inject
    private KeyboardHandler keyboardHandler;

    @Inject
    private MouseManager mouseManager;
    @Inject
    private MouseHandler mouseHandler;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    @Named("developerMode")
    private boolean developerMode;

    @Getter
    @Setter
    private Point menuOpenPoint;

    private NarrationPluginPanel panel;
    private NavigationButton navButton;
    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> this.config.narrateHotkey()) {
        @Override
        public void hotkeyPressed() {
            keyboardHandler.handleHotkey(config.narrateHotkey());
        }
    };
    private final HotkeyListener quantityHotkeyListener = new HotkeyListener(() -> this.config.narrateQuantityHotkey()) {
        @Override
        public void hotkeyPressed() {
            keyboardHandler.handleHotkey(config.narrateQuantityHotkey());
        }
    };

    @Override
    protected void startUp() {
        // TODO: say use actions
        // TODO: consolidate hotkey vs click message processing
        log.info("Narration Plugin started!");
        this.keyManager.registerKeyListener(this.hotkeyListener);
        this.keyManager.registerKeyListener(this.quantityHotkeyListener);
        this.mouseManager.registerMouseListener(this.mouseHandler);
        if (this.developerMode) {
            this.panel = this.injector.getInstance(NarrationPluginPanel.class);
            final BufferedImage icon = ImageUtil.loadImageResource(NarrationPlugin.class, "/narration_icon.png");
            this.navButton = NavigationButton.builder()
                .tooltip("Narration")
                .icon(icon)
                .priority(1)
                .panel(this.panel)
                .build();
            this.clientToolbar.addNavigation(this.navButton);
        }
    }

    @Override
    protected void shutDown() {
        this.clientToolbar.removeNavigation(this.navButton);
        this.keyManager.unregisterKeyListener(this.hotkeyListener);
        this.keyManager.unregisterKeyListener(this.quantityHotkeyListener);
        this.mouseManager.unregisterMouseListener(this.mouseHandler);
        log.info("Narration Plugin stopped!");
    }

    @Provides
    NarrationConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(NarrationConfig.class);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        // Read examine messages
        if (!this.config.enableOnExamine())
            return;
        switch (chatMessage.getType()) {
            case ITEM_EXAMINE:
            case NPC_EXAMINE:
            case OBJECT_EXAMINE:
                this.ttsSegmentProcessor.add(new MessageSegment(chatMessage.getMessage()));
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        this.ttsSegmentProcessor.process();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("narration") && configChanged.getKey().equals("test")) {
            this.ttsSegmentProcessor.clear();
            this.ttsSegmentProcessor.add(new MessageSegment("The quick brown fox jumped over the lazy dog."));
        }
    }

    @Subscribe
    public void onMenuOpened(MenuOpened menuOpened) {
        if (!this.config.enableOnMenu())
            return;

        StringBuilder sb = new StringBuilder();
        final MenuEntry[] entries = menuOpened.getMenuEntries();
        for (int i = entries.length - 1; i >= 0; i--) {
            this.sayMenuEntry(entries[i]);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        boolean blacklist = menuOptionClicked.getMenuAction() != MenuAction.WALK &&
            menuOptionClicked.getMenuAction() != MenuAction.CANCEL &&
            menuOptionClicked.getMenuAction() != MenuAction.WIDGET_TYPE_6;

        if (blacklist) {
            log.debug("clearing the last segment");
            this.ttsSegmentProcessor.clearLastSegment();
        }

        if (!this.config.enableOnClick())
            return;

        // If the menu is open, and you click on a menu option, say it
        // If the menu is not open (clicking on something), only say it if it is not Walk, Cancel, or a dialog option
        if (this.client.isMenuOpen() || blacklist) {
            this.sayMenuOptionClicked(menuOptionClicked);
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (!this.config.enableDialog())
            return;

        this.clientThread.invokeLater(() -> {
            String speakerName = null;
            String text = null;
            switch (widgetLoaded.getGroupId()) {
                case WidgetID.DIALOG_PLAYER_GROUP_ID: {
                    Widget dialogWidget = this.client.getWidget(WidgetInfo.DIALOG_PLAYER_TEXT);
                    if (dialogWidget == null)
                        return;
                    speakerName = this.config.usePlayerName() ? client.getLocalPlayer().getName() : "You";
                    text = dialogWidget.getText();
                    break;
                }
                case WidgetID.DIALOG_NPC_GROUP_ID: {
                    Widget dialogWidget = this.client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
                    if (dialogWidget == null)
                        return;
                    Widget speakerNameWidget = this.client.getWidget(WidgetInfo.DIALOG_NPC_NAME);
                    speakerName = speakerNameWidget != null ? speakerNameWidget.getText() : "";
                    text = dialogWidget.getText();
                    break;
                }
                case WidgetID.DIALOG_OPTION_GROUP_ID: {
                    Widget dialogWidget = this.client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);
                    if (dialogWidget == null)
                        return;
                    Widget[] children = dialogWidget.getChildren();
                    if (children == null)
                        return;
                    StringBuilder sb = new StringBuilder();
                    int i = 0;
                    for (Widget widget : children) {
                        String widgetText = widget.getText();
                        if (widgetText.isEmpty())
                            break;
                        if (i == 0)
                            speakerName = widgetText;
                        else
                            sb.append(i).append(". ").append(widgetText).append(". ");
                        i++;
                    }
                    text = sb.toString();
                    break;
                }
                case WidgetID.LEVEL_UP_GROUP_ID: {
                    Widget lvlDialog = this.client.getWidget(WidgetInfo.LEVEL_UP_LEVEL);
                    Widget skillDialog = this.client.getWidget(WidgetInfo.LEVEL_UP_SKILL);
                    if (lvlDialog == null || skillDialog == null)
                        return;
                    log.debug(lvlDialog.getText());
                    log.debug(skillDialog.getText());
                    speakerName = lvlDialog.getText();
                    text = skillDialog.getText();
                    break;
                }
                // Cat age
                case 229: {
                    Widget textWidget = this.client.getWidget(229, 1);
                    if (textWidget == null)
                        return;
                    speakerName = "";
                    text = textWidget.getText();
                }
            }

            if (speakerName == null || text == null)
                return;

            if (!this.config.enableQueueDialog()) {
                log.debug("clearing processor");
                this.ttsSegmentProcessor.clear();
            }
            log.debug("adding: " + text);
            this.ttsSegmentProcessor.add(new DialogSegment(text, speakerName, this.config.saySpeakerName()));
        });
    }

    private void sayMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        String actionName = menuOptionClicked.getMenuOption();
        String itemName = menuOptionClicked.getMenuTarget();
        log.debug(String.format("param1: %s, param0: %s", menuOptionClicked.getParam1(), menuOptionClicked.getParam0()));
        final Widget widget = this.client.getWidget(menuOptionClicked.getParam1());
        if (widget != null) {
            if (widget.getChildren() != null) {
                log.debug("has children");
                if (widget.getParent().getId() == WidgetInfo.BANK_PIN_CONTAINER.getId()) {
                    actionName = widget.getChild(1).getText();
                } else if (widget.getId() == WidgetInfo.PACK(553, 14)) { // Report reason
                    actionName = widget.getChild(menuOptionClicked.getParam0() + 1).getText() + " " +
                        widget.getChild(menuOptionClicked.getParam0() + 2).getText();
                    // In bank ui (maybe other things too like deposit boxes or things like that?)
                } else {
                    Widget child = widget.getChild(menuOptionClicked.getParam0());
                    if (child != null && child.getItemId() > -1) {
                        itemName = this.itemManager.getItemComposition(child.getItemId()).getName();
                    }
                }
            } else if (widget.getParent().getId() == WidgetInfo.PACK(553, 7)) { // Report add to ignore
                actionName = this.client.getWidget(553, 8).getText();
                // normal inventory
            } else if (widget.getId() == WidgetInfo.INVENTORY.getId()) {
                WidgetItem itemWidget = widget.getWidgetItem(menuOptionClicked.getParam0());
                int itemID = itemWidget.getId();
                ItemComposition item = this.itemManager.getItemComposition(itemID);
                itemName = item.getName();
                // Fallback
            } else if (menuOptionClicked.getParam0() > -1) {
                itemName = widget.getChild(menuOptionClicked.getParam0()).getText();
            }
//		} else if (menuOptionClicked.getMenuAction() == MenuAction.EXAMINE_OBJECT) {
//		} else if (menuOptionClicked.getMenuAction() == MenuAction.EXAMINE_NPC) {
//			this.npcManager.getNpcInfo(menuOptionClicked.getParam0());
        }
        log.debug("itemName is set to: " + itemName);

        this.ttsSegmentProcessor.add(new MessageSegment(actionName + " " + itemName));
    }

    private void sayMenuEntry(MenuEntry menuEntry) {
        this.ttsSegmentProcessor.add(new MenuEntrySegment(menuEntry));
    }

}
