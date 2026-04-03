package com.adamk33n3r.runelite.watchdog.ui.panels;

import com.adamk33n3r.runelite.watchdog.alerts.*;
import com.adamk33n3r.runelite.watchdog.ui.alerts.*;

import net.runelite.api.Client;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Creates {@link AlertContentPanel} instances for any alert type.
 * Used by {@link com.adamk33n3r.runelite.watchdog.ui.nodegraph.AlertNodePanel} to populate
 * type-specific controls without duplicating UI code.
 */
@Slf4j
@Singleton
public class AlertPanelContentFactory {
    @Inject private Client client;

    /**
     * Creates the type-specific content panel for {@code alert}.
     * The returned panel can be added to any container (node panel or sidebar).
     *
     * @param alert    the alert to create a content panel for
     * @param onChange callback fired whenever the user changes a value
     * @return the content panel, or {@code null} if the type is not registered
     */
    public AlertContentPanel<?> createContentPanel(Alert alert, Runnable onChange) {
        if (alert instanceof ChatAlert)
            return new GameMessageAlertPanel((ChatAlert) alert, onChange);
        if (alert instanceof PlayerChatAlert)
            return new PlayerChatAlertPanel((PlayerChatAlert) alert, onChange);
        if (alert instanceof SpawnedAlert)
            return new SpawnedAlertPanel((SpawnedAlert) alert, onChange);
        if (alert instanceof StatChangedAlert)
            return new StatChangedAlertPanel((StatChangedAlert) alert, onChange);
        if (alert instanceof InventoryAlert)
            return new InventoryAlertPanel((InventoryAlert) alert, onChange);
        if (alert instanceof LocationAlert)
            return new LocationAlertPanel((LocationAlert) alert, this.client, onChange);
        if (alert instanceof XPDropAlert)
            return new XPDropAlertPanel((XPDropAlert) alert, onChange);
        if (alert instanceof OverheadTextAlert)
            return new OverheadTextAlertPanel((OverheadTextAlert) alert, onChange);
        if (alert instanceof SoundFiredAlert)
            return new SoundFiredAlertPanel((SoundFiredAlert) alert, onChange);
        if (alert instanceof NotificationFiredAlert)
            return new NotificationFiredAlertPanel((NotificationFiredAlert) alert, onChange);
        if (alert instanceof AdvancedAlert)
            return new AdvancedAlertPanel((AdvancedAlert) alert, onChange);

        log.warn("No AlertContentPanel registered for alert type: {}", alert.getClass().getSimpleName());
        return null;
    }
}
