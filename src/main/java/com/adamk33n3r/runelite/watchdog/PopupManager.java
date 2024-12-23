package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.notifications.Popup;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.WidgetNode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayDeque;
import java.util.Queue;

@Getter
@Singleton
public class PopupManager {
    private static final int SCRIPT_ID = 3343; // NOTIFICATION_DISPLAY_INIT

    private static final int RESIZABLE_CLASSIC_LAYOUT = WidgetUtil.packComponentId(161, 13);
    private static final int RESIZABLE_MODERN_LAYOUT = WidgetUtil.packComponentId(164, 13);
    private static final int FIXED_CLASSIC_LAYOUT = WidgetUtil.packComponentId(548, 42);

    private static final int INTERFACE_ID = 660;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    private final Queue<Popup.PopupData> popupQueue = new ArrayDeque<>();

    public void processPopupQueue() {
        // Wait for there to not be any popups
        if (this.client.getWidget(INTERFACE_ID, 1) != null) {
            return;
        }

        if (!this.popupQueue.isEmpty()) {
            Popup.PopupData popupData = this.popupQueue.poll();

            WidgetNode widgetNode = this.client.openInterface(this.getComponentID(), INTERFACE_ID, WidgetModalMode.MODAL_CLICKTHROUGH);
            Widget widget = this.client.getWidget(INTERFACE_ID, 1);

            // Doing this instead of textColor.getRGB() so that it doesn't use the alpha channel and cause WHITE to be -1
            int color = popupData.color == null ? -1 : popupData.color.getRed() << 16 | popupData.color.getGreen() << 8 | popupData.color.getBlue();
            this.client.runScript(SCRIPT_ID, popupData.title, popupData.message, color);

            this.clientThread.invokeLater(() -> {
                assert widget != null;

                // Wait until the popup has closed
                if (widget.getWidth() > 0) {
                    return false;
                }

                this.client.closeInterface(widgetNode, true);
                return true;
            });
        }
    }

    private int getComponentID() {
        return this.client.isResized()
            ? (client.getVarbitValue(Varbits.SIDE_PANELS) == 1
            ? RESIZABLE_MODERN_LAYOUT
            : RESIZABLE_CLASSIC_LAYOUT)
            : FIXED_CLASSIC_LAYOUT;

    }
}
