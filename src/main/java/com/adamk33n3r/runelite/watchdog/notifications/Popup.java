package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.PopupManager;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.inject.Inject;
import java.awt.Color;

import static com.adamk33n3r.runelite.watchdog.WatchdogConfig.DEFAULT_NOTIFICATION_TEXT_COLOR;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Popup extends MessageNotification {
    private Color textColor = DEFAULT_NOTIFICATION_TEXT_COLOR;

    private transient PopupManager popupManager;

    @Inject
    public Popup(WatchdogConfig config) {
        super(config);
        this.popupManager = WatchdogPlugin.getInstance().getPopupManager();
        this.setDefaults();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.popupManager.getPopupQueue().offer(new PopupData(
            this.getAlert().getName(),
            this.getMessage(),
            this.getTextColor()
        ));
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        this.setTextColor(this.watchdogConfig.defaultPopupTextColor());
    }

    @AllArgsConstructor
    public static class PopupData {
        public String title;
        public String message;
        public Color color;
    }
}
