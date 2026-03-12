package com.adamk33n3r.runelite.watchdog.ui.alerts;

import com.adamk33n3r.runelite.watchdog.WatchdogPanel;
import com.adamk33n3r.runelite.watchdog.alerts.LocationAlert;
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
        JPanel pointPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JPanel xPos = PanelUtils.createLabeledComponent(
            "X Pos",
            "The X position",
            PanelUtils.createSpinner(this.alert.getWorldPoint().getX(),
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                1,
                (val) -> this.alert.setWorldPoint(this.alert.getWorldPoint().dx(val - this.alert.getWorldPoint().getX())))
        , true);
        JPanel yPos = PanelUtils.createLabeledComponent(
            "Y Pos",
            "The Y position",
            PanelUtils.createSpinner(this.alert.getWorldPoint().getY(),
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                1,
                (val) -> this.alert.setWorldPoint(this.alert.getWorldPoint().dy(val - this.alert.getWorldPoint().getY())))
        , true);
        JPanel plane = PanelUtils.createLabeledComponent(
            "Plane",
            "The plane number",
            PanelUtils.createSpinner(this.alert.getWorldPoint().getPlane(),
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                1,
                (val) -> this.alert.setWorldPoint(this.alert.getWorldPoint().dz(val - this.alert.getWorldPoint().getPlane())))
            , true);
        pointPanel.add(xPos);
        pointPanel.add(yPos);
        pointPanel.add(plane);

        JPanel checkboxes = new JPanel(new GridLayout(1, 0, 5, 5));
        checkboxes.add(PanelUtils.createCheckbox("Cardinal Only", "Only fire on cardinal directions", this.alert.isCardinalOnly(), this.alert::setCardinalOnly));
        checkboxes.add(PanelUtils.createCheckbox("Repeat", "Repeat alert while standing in area", this.alert.isRepeat(), this.alert::setRepeat));

        this.addAlertDefaults()
            .addSubPanelControl(pointPanel)
            .addButton("Set to Current", "Set world point to current position", (btn, mod) -> {
                if (this.client.getLocalPlayer() != null) {
                    WorldPoint worldPoint = WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation());
                    this.alert.setWorldPoint(worldPoint);
                    this.rebuild();
                }
            })
            .addSpinner("Distance", "Minimum distance to the set location to trigger this alert", this.alert.getDistance(), this.alert::setDistance, 0, Integer.MAX_VALUE, 1)
            .addSubPanelControl(checkboxes)
            .addNotifications();
    }
}
