package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.AlertManager;
import com.adamk33n3r.runelite.watchdog.Displayable;
import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class AlertToggle extends Notification implements RegexMatcher {
    @Accessors(chain = false)
    private String pattern;
    @Accessors(chain = false)
    private boolean regexEnabled = false;
    private ToggleMode mode = ToggleMode.TOGGLE;

    @Inject
    private transient AlertManager alertManager;

    @Override
    protected void fireImpl(String[] triggerValues) {
        this.alertManager.getAllAlerts()
            .filter(alert -> Util.matchPattern(this, alert.getName()) != null)
            .forEach(alert -> {
                alert.setEnabled(this.mode == ToggleMode.ENABLE || (this.mode == ToggleMode.TOGGLE && !alert.isEnabled()));
            });
    }

    @Getter
    @AllArgsConstructor
    public enum ToggleMode implements Displayable {
        ENABLE("Enable", "Enable the alert"),
        DISABLE("Disable", "Disable the alert"),
        TOGGLE("Toggle", "Toggle the alert"),
        ;

        private final String name;
        private final String tooltip;
    }
}
