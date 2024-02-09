package com.adamk33n3r.runelite.watchdog;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class Timeout {
    BiConsumer<Timeout, Boolean> task;
    ScheduledFuture<?> future;
    public Timeout(ScheduledExecutorService executor, BiConsumer<Timeout, Boolean> task, long delay, TimeUnit unit) {
        this.task = task;
        this.future = executor.schedule(() -> this.task.accept(this, false), delay, unit);
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
        this.task.accept(this, true);
    }
}
