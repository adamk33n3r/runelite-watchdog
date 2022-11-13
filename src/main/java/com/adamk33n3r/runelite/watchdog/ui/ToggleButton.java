/*
 * Adapted from net.runelite.client.plugins.config.PluginToggleButton
 */

package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ToggleButton extends JToggleButton {
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;

    static {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(ConfigPlugin.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(onSwitcher);
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(
            ImageUtil.luminanceScale(
                ImageUtil.grayscaleImage(onSwitcher),
                0.61f
            ),
            true,
            false
        ));
    }

    public ToggleButton() {
        super(OFF_SWITCHER);
        this.setSelectedIcon(ON_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
        this.setPreferredSize(new Dimension(25, 0));
        this.addItemListener(l -> this.updateTooltip());
        this.updateTooltip();
    }

    private void updateTooltip() {
        this.setToolTipText(this.isSelected() ? "Disable alert" :  "Enable alert");
    }
}

