package com.adamk33n3r.runelite.watchdog.ui;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;

public abstract class Icons {
    public static final ImageIcon ADD = new ImageIcon(ImageUtil.loadImageResource(TimeTrackingPlugin.class, "add_icon.png"));

    public static final ImageIcon HELP = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "help_icon.png"), 80));
    public static final ImageIcon HELP_HOVER = new ImageIcon(ImageUtil.luminanceOffset(HELP.getImage(), -80));

    public static final ImageIcon HISTORY = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_history.png"));
    public static final ImageIcon HISTORY_HOVER = new ImageIcon(ImageUtil.luminanceOffset(HISTORY.getImage(), -80));
    public static final ImageIcon TOOLS = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_tools.png"));
    public static final ImageIcon TOOLS_HOVER = new ImageIcon(ImageUtil.luminanceOffset(TOOLS.getImage(), -80));

    public static final ImageIcon DISCORD = new ImageIcon(ImageUtil.loadImageResource(InfoPanel.class, "discord_icon.png"));
    public static final ImageIcon DISCORD_HOVER = new ImageIcon(ImageUtil.luminanceOffset(DISCORD.getImage(), -100));

    public static final ImageIcon KOFI = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "kofi_icon.png"));
    public static final ImageIcon KOFI_HOVER = new ImageIcon(ImageUtil.luminanceOffset(KOFI.getImage(), -100));

    public static final ImageIcon CONFIG = new ImageIcon(ImageUtil.resizeImage(ImageUtil.loadImageResource(Icons.class, "mdi_cog.png"), 14, 14));
    public static final ImageIcon CONFIG_HOVER = new ImageIcon(ImageUtil.luminanceOffset(CONFIG.getImage(), -100));

    public static final ImageIcon EXPORT = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_export-variant.png"));
    public static final ImageIcon EXPORT_HOVER = new ImageIcon(ImageUtil.alphaOffset(EXPORT.getImage(), -120));
    public static final ImageIcon IMPORT = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_import.png"));
    public static final ImageIcon IMPORT_HOVER = new ImageIcon(ImageUtil.alphaOffset(IMPORT.getImage(), -120));
    public static final ImageIcon DELETE = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_delete.png"));
    public static final ImageIcon DELETE_HOVER = new ImageIcon(ImageUtil.luminanceOffset(DELETE.getImage(), -80));
    public static final ImageIcon DOWNLOAD = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_download.png"));

    public static final ImageIcon CLONE = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_content-duplicate.png"));
    public static final ImageIcon CLONE_HOVER = new ImageIcon(ImageUtil.alphaOffset(CLONE.getImage(), -120));
    public static final ImageIcon EDIT = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_pencil.png"));
    public static final ImageIcon EDIT_HOVER = new ImageIcon(ImageUtil.alphaOffset(EDIT.getImage(), -120));
    public static final ImageIcon DRAG_VERT = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_drag-vertical.png"));

    public static final ImageIcon REFRESH = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_refresh.png"));
    public static final ImageIcon REFRESH_HOVER = new ImageIcon(ImageUtil.luminanceOffset(REFRESH.getImage(), -80));

    /*
     * Alerts
     */
    public static final ImageIcon BACK = new ImageIcon(ImageUtil.loadImageResource(ConfigPlugin.class, "config_back_icon.png"));
    public static final ImageIcon BACK_HOVER = new ImageIcon(ImageUtil.alphaOffset(BACK.getImage(), -120));
    public static final ImageIcon REGEX = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_regex.png"), -80));
    public static final ImageIcon REGEX_HOVER = new ImageIcon(ImageUtil.luminanceOffset(REGEX.getImage(), -40));
    public static final ImageIcon REGEX_SELECTED = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_regex-outline.png"));
    public static final ImageIcon REGEX_SELECTED_HOVER = new ImageIcon(ImageUtil.luminanceOffset(REGEX_SELECTED.getImage(), -80));
    public static final ImageIcon PICKER = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_eyedropper.png"));
    public static final ImageIcon PICKER_HOVER = new ImageIcon(ImageUtil.luminanceOffset(PICKER.getImage(), -120));

    /*
     * Notifications
     */
    public static final ImageIcon FOREGROUND = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_flip-to-front.png"));
    public static final ImageIcon FOREGROUND_HOVER = new ImageIcon(ImageUtil.luminanceOffset(FOREGROUND.getImage(), -80));
    public static final ImageIcon BACKGROUND = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_flip-to-back.png"));
    public static final ImageIcon BACKGROUND_HOVER = new ImageIcon(ImageUtil.luminanceOffset(BACKGROUND.getImage(), -80));
    public static final ImageIcon AFK = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "afk_icon.png"));
    public static final ImageIcon AFK_HOVER = new ImageIcon(ImageUtil.luminanceOffset(AFK.getImage(), -80));
    public static final ImageIcon NON_AFK = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "non_afk_icon.png"));
    public static final ImageIcon NON_AFK_HOVER = new ImageIcon(ImageUtil.luminanceOffset(NON_AFK.getImage(), -80));
    public static final ImageIcon TEST = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_flask.png"));
    public static final ImageIcon TEST_HOVER = new ImageIcon(ImageUtil.luminanceOffset(TEST.getImage(), -80));
    public static final ImageIcon VOLUME = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_volume-high.png"), -80));
    public static final ImageIcon CLOCK = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_clock-outline.png"), -80));
    public static final ImageIcon SPEECH = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_account-voice.png"), -80));
    public static final ImageIcon SPEED = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_speedometer.png"), -80));
    public static final ImageIcon TIMER_PLUS = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_timer-plus-outline.png"));
    public static final ImageIcon TIMER_PLUS_HOVER = new ImageIcon(ImageUtil.luminanceOffset(TIMER_PLUS.getImage(), -80));
    public static final ImageIcon TIMER_REMOVE = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_timer-remove-outline.png"));
    public static final ImageIcon TIMER_REMOVE_HOVER = new ImageIcon(ImageUtil.luminanceOffset(TIMER_REMOVE.getImage(), -80));
    public static final ImageIcon BORDER_OUTSIDE = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_border-outside.png"), -80));
    public static final ImageIcon DICE_MULTIPLE = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_dice-multiple.png"));
    public static final ImageIcon DICE_MULTIPLE_HOVER = new ImageIcon(ImageUtil.luminanceOffset(DICE_MULTIPLE.getImage(), -80));
    public static final ImageIcon DICE_MULTIPLE_OFF = new ImageIcon(ImageUtil.luminanceOffset(ImageUtil.loadImageResource(Icons.class, "mdi_dice-multiple-outline.png"), -80));
    public static final ImageIcon DICE_MULTIPLE_OFF_HOVER = new ImageIcon(ImageUtil.luminanceOffset(DICE_MULTIPLE_OFF.getImage(), -40));
}
