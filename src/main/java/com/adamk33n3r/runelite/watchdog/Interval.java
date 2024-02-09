package com.adamk33n3r.runelite.watchdog;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class Interval extends Timeout {
    public Interval(ScheduledExecutorService executor, BiConsumer<Timeout, Boolean> task, long delay, TimeUnit unit) {
        super();
        this.task = task;
        this.future = executor.scheduleAtFixedRate(() -> task.accept(this, false), 0, delay, unit);
    }
}
