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

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Dink extends MessageNotification {
    private boolean includeScreenshot = false;
    private String urls = null;

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
        if (urls != null && !urls.isEmpty()) {
            List<HttpUrl> httpUrls = Arrays.stream(urls.split(";")).map(HttpUrl::parse).collect(Collectors.toList());
            dinkData.put("urls", httpUrls);
        }

        log.debug("Sending dink notification with data: {}", dinkData);

        this.eventBus.post(new PluginMessage("dink", "notify", dinkData));
    }
}
