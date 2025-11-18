package com.adamk33n3r.runelite.watchdog.notifications;

import com.adamk33n3r.runelite.watchdog.Util;
import com.adamk33n3r.runelite.watchdog.WatchdogPlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;

import javax.inject.Inject;
import java.util.HashMap;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ShortestPath extends Notification {
    private ShortestPathMode mode = ShortestPathMode.PATH;
    private boolean useCurrentLocationForStart = true;
    private WorldPoint start = new WorldPoint(3223, 3219, 0);
    private WorldPoint target = new WorldPoint(2897, 3543, 0);

    @Inject
    private transient EventBus eventBus;

    @Inject
    private transient ClientThread clientThread;

    public boolean isPathMode() {
        return this.mode == ShortestPathMode.PATH;
    }

    public boolean isClearMode() {
        return this.mode == ShortestPathMode.CLEAR;
    }

    @Override
    protected void fireImpl(String[] triggerValues) {
        switch (this.mode) {
            case CLEAR:
                this.clientThread.invoke(() -> {
                    this.eventBus.post(new PluginMessage("shortestpath", "clear"));
                });
                break;
            case PATH:
                HashMap<String, Object> data = new HashMap<>();
                data.put("start", this.useCurrentLocationForStart ? -1 : this.start);
                data.put("target", this.target);

                log.debug("Setting shortest path with data: {}", data);

                this.clientThread.invoke(() -> {
                    this.eventBus.post(new PluginMessage("shortestpath", "path", data));
                });
                break;
        }
    }
}
