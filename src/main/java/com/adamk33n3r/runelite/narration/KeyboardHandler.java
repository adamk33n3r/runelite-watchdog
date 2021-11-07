package com.adamk33n3r.runelite.narration;

import com.adamk33n3r.runelite.narration.tts.MenuEntrySegment;
import com.adamk33n3r.runelite.narration.tts.MessageSegment;
import com.adamk33n3r.runelite.narration.tts.TTSSegmentProcessor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.Keybind;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import java.util.function.Supplier;

@Slf4j
public class KeyboardHandler {
    private static final int MENU_OPTION_HEIGHT = 15;
    private static final int MENU_EXTRA_TOP = 4;
    private static final int MENU_EXTRA_BOTTOM = 3;
    private static final int MENU_BORDERS_TOTAL = MENU_EXTRA_TOP + MENU_OPTION_HEIGHT + MENU_EXTRA_BOTTOM;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;
    @Inject
    private NarrationPlugin plugin;
    @Inject
    private NarrationConfig config;
    @Inject
    private TTSSegmentProcessor ttsSegmentProcessor;

    public void handleHotkey(Keybind keybind) {
        // This solves an issue where sometimes client.getMenuEntries().last() was "Examine object-behind-widget" ¯\_(ツ)_/¯
        this.clientThread.invokeLater(() -> {
            MenuEntry[] entries = this.client.getMenuEntries();
            int idx = entries.length - 1;
            if (this.config.narrateQuantityHotkey() == keybind) {
                if (idx == -1)
                    return;
                MenuEntry hoveredEntry = entries[idx];
                Widget hoveredWidget = this.client.getWidget(hoveredEntry.getParam1());
                if (hoveredWidget != null) {
                    Widget childWidget = hoveredWidget.getChild(hoveredEntry.getParam0());
                    if (childWidget != null && childWidget.getItemId() != -1) {
                        this.ttsSegmentProcessor.add(new MessageSegment(childWidget.getItemQuantity() + ""));
                    } else if (hoveredWidget.getId() == WidgetInfo.INVENTORY.getId()) {
                        WidgetItem itemWidget = hoveredWidget.getWidgetItem(hoveredEntry.getParam0());
                        int quantity = itemWidget.getQuantity();
                        this.ttsSegmentProcessor.add(new MessageSegment(quantity + ""));
                    }
                }
            } else if (this.config.narrateHotkey() == keybind) {
                if (this.client.isMenuOpen()) {
                    // Shamelessly stolen from contextual-cursor :)
                    final int menuTop;
                    final int menuHeight = (entries.length * MENU_OPTION_HEIGHT) + MENU_BORDERS_TOTAL;
                    if (menuHeight + this.plugin.getMenuOpenPoint().getY() > this.client.getCanvasHeight()) {
                        menuTop = this.client.getCanvasHeight() - menuHeight;
                    } else {
                        menuTop = this.plugin.getMenuOpenPoint().getY();
                    }

                    final int fromTop = Math.max((this.client.getMouseCanvasPosition().getY() - MENU_EXTRA_TOP) - menuTop, MENU_OPTION_HEIGHT);

                    idx = entries.length - (fromTop / MENU_OPTION_HEIGHT);
                }
                idx = Math.min(entries.length - 1, Math.max(0, idx));
                MenuEntry hoveredEntry = entries[idx];
                Widget hoveredWidget = this.client.getWidget(hoveredEntry.getParam1());
                log.debug("param1: " + hoveredEntry.getParam1());
                log.debug("param0: " + hoveredEntry.getParam0());
                if (hoveredWidget != null && hoveredWidget.getId() == WidgetInfo.PACK(553, 14)) { // Report reason
                    this.ttsSegmentProcessor.add(new MessageSegment(
                        hoveredWidget.getChild(hoveredEntry.getParam0() + 1).getText() + " " +
                            hoveredWidget.getChild(hoveredEntry.getParam0() + 2).getText()
                    ));
                } else if (hoveredWidget != null && hoveredWidget.getParent().getId() == WidgetInfo.PACK(553, 7)) { // Report add to ignore
                    this.ttsSegmentProcessor.add(new MessageSegment(this.client.getWidget(553, 8).getText()));
                } else if (hoveredWidget != null && hoveredWidget.getParent() != null && hoveredWidget.getParent().getId() == WidgetInfo.BANK_PIN_CONTAINER.getId()) {
                    String number = hoveredWidget.getChild(1).getText();
                    this.ttsSegmentProcessor.add(new MessageSegment(number));
                } else if (hoveredWidget != null && hoveredWidget.getId() == WidgetInfo.INVENTORY.getId()) {
//                    WidgetItem itemWidget = finalHoveredWidget.getWidgetItem(finalHoveredEntry.getParam0());
//                    int itemID = itemWidget.getId();
//                    ItemComposition item = this.itemManager.getItemComposition(itemID);
//                    this.ttsSegmentProcessor.add(new MessageSegment(finalHoveredEntry.getOption() + " " + item.getName()));
                    this.ttsSegmentProcessor.add(new MenuEntrySegment(hoveredEntry));
                } else {
                    if (this.client.isMenuOpen() || (
                        hoveredEntry.getType() != MenuAction.WALK.getId() && hoveredEntry.getType() != MenuAction.CANCEL.getId())
                    ) {
                        this.ttsSegmentProcessor.add(new MenuEntrySegment(hoveredEntry));
                    }
                }
            }
        });
    }
}
