package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PluginConfigChange extends Notification {
    private String pluginName;
    private String configKey;
    private String data;

    @Inject
    private transient ConfigManager configManager;

    @Override
    protected void fireImpl(String[] triggerValues) {
        var val = Util.processTriggerValues(this.data, triggerValues);
        log.debug("Setting plugin config {}.{} to {}", this.pluginName, this.configKey, val);
        this.configManager.setConfiguration(this.pluginName, this.configKey, val);
    }
}
