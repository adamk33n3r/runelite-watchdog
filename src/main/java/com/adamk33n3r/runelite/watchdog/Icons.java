package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.util.ImageUtil;

import javax.swing.ImageIcon;

public abstract class Icons {
    public static final ImageIcon EDIT_ICON;
    public static final ImageIcon EXPORT_ICON;
    public static final ImageIcon IMPORT_ICON;
    public static final ImageIcon DELETE_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_delete.png"));
    public static final ImageIcon DELETE_ICON_OUTLINE;
    public static final ImageIcon DOWNLOAD_ICON;
    public static final ImageIcon DOWNLOAD_BOX_ICON;
    public static final ImageIcon DOWNLOAD_OUTLINE_ICON;
    public static final ImageIcon EDIT_OUTLINE_ICON;
    public static final ImageIcon CLONE_ICON = new ImageIcon(ImageUtil.loadImageResource(ConfigPlugin.class, "mdi_content-duplicate.png"));

    static {
        EXPORT_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_database-export.png"));
        IMPORT_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_database-import.png"));
        DELETE_ICON_OUTLINE = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_delete-outline.png"));
        DOWNLOAD_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_download.png"));
        DOWNLOAD_BOX_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_download-box-outline.png"));
        DOWNLOAD_OUTLINE_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_download-outline.png"));
        EDIT_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_pencil.png"));
        EDIT_OUTLINE_ICON = new ImageIcon(ImageUtil.loadImageResource(Icons.class, "mdi_pencil-outline.png"));
    }
}
