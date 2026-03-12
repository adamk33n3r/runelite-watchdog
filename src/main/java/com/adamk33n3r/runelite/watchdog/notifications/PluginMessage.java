package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.util.HashMap;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PluginMessage extends Notification {
    private String namespace;
    private String name;
    private String data;

    @Inject
    private transient EventBus eventBus;

    @Inject
    private transient ClientThread clientThread;

    @Inject
    private transient Gson gson;

    @Override
    protected void fireImpl(String[] triggerValues) {
        // Wrapping in client thread for safety
        this.clientThread.invoke(() -> {
            if (Strings.isNullOrEmpty(this.data)) {
                this.eventBus.post(new net.runelite.client.events.PluginMessage(this.namespace, this.name));
                return;
            }

            try {
                HashMap<String, Object> dataObj = gson.fromJson(
                    Util.processTriggerValues(this.data, triggerValues),
                    new TypeToken<HashMap<String, Object>>() {
                    }.getType()
                );
                this.eventBus.post(new net.runelite.client.events.PluginMessage(this.namespace, this.name, dataObj));
            } catch (JsonSyntaxException ex) {
                log.error("Invalid plugin message data. sending empty data", ex);
                this.eventBus.post(new net.runelite.client.events.PluginMessage(this.namespace, this.name));
            }
        });
    }
}
