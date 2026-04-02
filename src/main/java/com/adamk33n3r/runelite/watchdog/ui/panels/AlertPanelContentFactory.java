package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.ui.alerts.*;

import net.runelite.api.Client;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Dispatches to the static {@code buildTypeContent} method on each alert panel subclass,
 * populating just the type-specific controls into the supplied {@link AlertContentBuilder}.
 * Used by {@link com.adamk33n3r.runelite.watchdog.ui.nodegraph.AlertNodePanel} so that every
 * alert type is fully supported in the node graph editor without duplicating UI code.
 */
@Slf4j
@Singleton
public class AlertPanelContentFactory {
    @Inject private Client client;

    /**
     * Populates type-specific controls for {@code alert} into {@code builder}.
     * The builder's container, onChange, and rebuild runnables are provided by the caller.
     */
    public void populateContent(Alert alert, AlertContentBuilder builder) {
        if (alert instanceof ChatAlert) {
            GameMessageAlertPanel.buildTypeContent((ChatAlert) alert, builder);
        } else if (alert instanceof PlayerChatAlert) {
            PlayerChatAlertPanel.buildTypeContent((PlayerChatAlert) alert, builder);
        } else if (alert instanceof SpawnedAlert) {
            SpawnedAlertPanel.buildTypeContent((SpawnedAlert) alert, builder);
        } else if (alert instanceof StatChangedAlert) {
            StatChangedAlertPanel.buildTypeContent((StatChangedAlert) alert, builder);
        } else if (alert instanceof InventoryAlert) {
            InventoryAlertPanel.buildTypeContent((InventoryAlert) alert, builder);
        } else if (alert instanceof LocationAlert) {
            LocationAlertPanel.buildTypeContent((LocationAlert) alert, builder, this.client);
        } else if (alert instanceof XPDropAlert) {
            XPDropAlertPanel.buildTypeContent((XPDropAlert) alert, builder);
        } else if (alert instanceof OverheadTextAlert) {
            OverheadTextAlertPanel.buildTypeContent((OverheadTextAlert) alert, builder);
        } else if (alert instanceof SoundFiredAlert) {
            SoundFiredAlertPanel.buildTypeContent((SoundFiredAlert) alert, builder);
        } else if (alert instanceof NotificationFiredAlert) {
            NotificationFiredAlertPanel.buildTypeContent((NotificationFiredAlert) alert, builder);
        } else {
            log.warn("No type content registered for alert type: {}", alert.getClass().getSimpleName());
        }
    }
}
