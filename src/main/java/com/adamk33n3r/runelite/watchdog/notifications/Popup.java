package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogConfig;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import com.google.common.base.Strings;
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
    private String title = "";
    private Color textColor = DEFAULT_NOTIFICATION_TEXT_COLOR;

    @Inject
    public Popup(WatchdogConfig config) {
        super(config);
        this.setDefaults();
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        String titleString = Strings.isNullOrEmpty(this.title) ? this.getAlert().getName() : this.title;
        String title = Util.processTriggerValues(titleString, triggerValues);
        String message = Util.processTriggerValues(this.message, triggerValues);
        WatchdogPlugin.getInstance().getPopupManager().getPopupQueue().offer(new PopupData(
            title,
            message,
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
