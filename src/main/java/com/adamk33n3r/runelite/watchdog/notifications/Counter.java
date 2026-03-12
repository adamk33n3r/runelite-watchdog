package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Counter extends Notification {
    private int value;
    @Override
    protected void fireImpl(String[] triggerValues) {
        this.value++;
        WatchdogPlugin.getInstance().getAlertManager().saveAlerts();
    }
}
