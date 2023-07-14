package com.adamk33n3r.runelite.watchdog.hub;

import lombok.Getter;
import net.runelite.client.ui.DynamicGridLayout;

import javax.swing.*;
import java.awt.*;

@Getter
public class AlertHubItem extends JPanel {
    private final AlertManifest manifest;

    public AlertHubItem(AlertManifest manifest) {
        this.manifest = manifest;

        this.setLayout(new DynamicGridLayout(0, 1, 5, 5));
        this.add(new JLabel(this.manifest.toString()));
        this.add(new JLabel(this.manifest.getAuthor()));
        this.add(new JLabel(this.manifest.getDescription()));
        this.add(new JLabel(this.manifest.getRepo().toString()));
    }
}
