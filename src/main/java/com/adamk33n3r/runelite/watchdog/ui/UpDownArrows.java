package com.adamk33n3r.runelite.watchdog.ui;

import com.adamk33n3r.runelite.watchdog.ui.panels.PanelUtils;

import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

public class UpDownArrows extends JPanel {
    private static final ImageIcon UP_CHEVRON_ICON;
    private static final ImageIcon UP_CHEVRON_ICON_HOVER;
    private static final ImageIcon DOWN_CHEVRON_ICON;
    private static final ImageIcon DOWN_CHEVRON_ICON_HOVER;
    private static final Dimension BTN_DIMENSION = new Dimension(10, 10);

    static {
        final BufferedImage downArrowImg = ImageUtil.loadImageResource(LootTrackerPlugin.class, "expanded.png");
        BufferedImage upArrowImg = ImageUtil.flipImage(downArrowImg, false, true);
        UP_CHEVRON_ICON = new ImageIcon(upArrowImg);
        UP_CHEVRON_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(upArrowImg, -80));
        DOWN_CHEVRON_ICON = new ImageIcon(downArrowImg);
        DOWN_CHEVRON_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(downArrowImg, -80));
    }

    public UpDownArrows(String upTooltip, PanelUtils.ButtonClickListener onUp, String downTooltip, PanelUtils.ButtonClickListener onDown) {
        this(upTooltip, onUp, downTooltip, onDown, false);
    }

    public UpDownArrows(String upTooltip, PanelUtils.ButtonClickListener onUp, String downTooltip, PanelUtils.ButtonClickListener onDown, boolean horizontal) {
        if (horizontal) {
            this.setLayout(new GridLayout(1, 2, 0, 0));
            this.setPreferredSize(new Dimension(30, 10));
        } else {
            this.setLayout(new GridLayout(2, 1, 0, 0));
            this.setPreferredSize(new Dimension(16, 16));
        }

        final JButton upButton = PanelUtils.createActionButton(UP_CHEVRON_ICON, UP_CHEVRON_ICON_HOVER, upTooltip, onUp);
        upButton.setPreferredSize(BTN_DIMENSION);
        this.add(upButton);
        final JButton downButton = PanelUtils.createActionButton(DOWN_CHEVRON_ICON, DOWN_CHEVRON_ICON_HOVER, downTooltip, onDown);
        downButton.setPreferredSize(BTN_DIMENSION);
        this.add(downButton);
    }
}
