package com.adamk33n3r.runelite.watchdog;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Interval extends Timeout {
    public Interval(ScheduledExecutorService executor, Runnable runnable, long delay, TimeUnit unit) {
        super();
        this.runnable = runnable;
        this.future = executor.scheduleAtFixedRate(runnable, 0, delay, unit);
    }
}
