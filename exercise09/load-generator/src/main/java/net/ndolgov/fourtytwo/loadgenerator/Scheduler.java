package net.ndolgov.fourtytwo.loadgenerator;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Scheduler {
    private final ScheduledExecutorService executor;

    private final long periodMs;

    public Scheduler(ScheduledExecutorService executor, long periodMs) {
        this.executor = executor;
        this.periodMs = periodMs;
    }

    void schedule(Runnable job) {
        executor.scheduleAtFixedRate(job, periodMs, periodMs, TimeUnit.MILLISECONDS);
    }

    void shutDown() {
        executor.shutdownNow();
    }
}
