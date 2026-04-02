package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.LocationAlert;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertContentBuilder;
import com.adamk33n3r.runelite.watchdog.ui.panels.AlertPanel;
import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import javax.swing.JPanel;
import java.awt.GridLayout;

public class LocationAlertPanel extends AlertPanel<LocationAlert> {
    private final Client client;

    public LocationAlertPanel(WatchdogPanel watchdogPanel, LocationAlert alert, Client client) {
        super(watchdogPanel, alert);
        this.client = client;
    }

    @Override
    protected void build() {
        this.addAlertDefaults();
        buildTypeContent(this.alert, new AlertContentBuilder(this.getControlContainer(), this.getSaveAction(), this::rebuild), this.client);
        this.addNotifications();
    }

    public static void buildTypeContent(LocationAlert alert, AlertContentBuilder builder, Client client) {
        JPanel pointPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JPanel xPos = PanelUtils.createLabeledComponent(
            "X Pos", "The X position",
            PanelUtils.createSpinner(alert.getWorldPoint().getX(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1,
                val -> alert.setWorldPoint(alert.getWorldPoint().dx(val - alert.getWorldPoint().getX()))),
            true);
        JPanel yPos = PanelUtils.createLabeledComponent(
            "Y Pos", "The Y position",
            PanelUtils.createSpinner(alert.getWorldPoint().getY(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1,
                val -> alert.setWorldPoint(alert.getWorldPoint().dy(val - alert.getWorldPoint().getY()))),
            true);
        JPanel plane = PanelUtils.createLabeledComponent(
            "Plane", "The plane number",
            PanelUtils.createSpinner(alert.getWorldPoint().getPlane(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1,
                val -> alert.setWorldPoint(alert.getWorldPoint().dz(val - alert.getWorldPoint().getPlane()))),
            true);
        pointPanel.add(xPos);
        pointPanel.add(yPos);
        pointPanel.add(plane);

        JPanel checkboxes = new JPanel(new GridLayout(1, 0, 5, 5));
        checkboxes.add(PanelUtils.createCheckbox("Cardinal Only", "Only fire on cardinal directions", alert.isCardinalOnly(), alert::setCardinalOnly));
        checkboxes.add(PanelUtils.createCheckbox("Repeat", "Repeat alert while standing in area", alert.isRepeat(), alert::setRepeat));

        builder
            .addSubPanelControl(pointPanel)
            .addButton("Set to Current", "Set world point to current position", (btn, mod) -> {
                if (client.getLocalPlayer() != null) {
                    WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
                    alert.setWorldPoint(worldPoint);
                    builder.rebuild();
                }
            })
            .addSpinner("Distance", "Minimum distance to the set location to trigger this alert", alert.getDistance(), alert::setDistance, 0, Integer.MAX_VALUE, 1)
            .addSubPanelControl(checkboxes);
    }
}
