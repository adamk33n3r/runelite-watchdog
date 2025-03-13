package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;
import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Dink extends MessageNotification {
    private boolean includeScreenshot = false;

    @Inject
    private transient EventBus eventBus;

    @Override
    protected void fireImpl(String[] triggerValues) {
        HashMap<String, Object> dinkData = new HashMap<>();
        String processedMessage = Util.processTriggerValues(this.message, triggerValues);
        dinkData.put("text", processedMessage);
        dinkData.put("sourcePlugin", WatchdogPlugin.getInstance().getName());
        dinkData.put("title", this.getAlert().getName());
        dinkData.put("imageRequested", this.includeScreenshot);

        log.debug("Sending dink notification with data: {}", dinkData);
        this.eventBus.post(new PluginMessage("dink", "notify", dinkData));
    }
}
