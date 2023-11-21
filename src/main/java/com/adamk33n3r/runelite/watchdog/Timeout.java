package com.adamk33n3r.runelite.watchdog;

import lombok.NoArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Timeout {
    Runnable runnable;
    ScheduledFuture<?> future;
    public Timeout(ScheduledExecutorService executor, Runnable runnable, long delay, TimeUnit unit) {
        this.runnable = runnable;
        this.future = executor.schedule(this.runnable, delay, unit);
    }
    protected Timeout() {}

    public void stop() {
        if (this.future.isDone()) {
            return;
        }
        this.future.cancel(true);
    }

    public void stopAndRunNow() {
        if (this.future.isDone()) {
            return;
        }
        this.future.cancel(true);
        this.runnable.run();
    }
}
