package net.ndolgov.exercise.renewer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * ScheduledExecutor-backed timer implementation
 */
public final class ScheduledExecutorTimer implements Timer {
    private final ScheduledExecutorService executor;

    private final long renewTimeoutMs;

    public ScheduledExecutorTimer(long renewTimeoutMs) {
        this.renewTimeoutMs = renewTimeoutMs;
        executor = Executors.newScheduledThreadPool(10);
    }

    public ScheduledExecutorTimer(ScheduledExecutorService executor, long renewTimeoutMs) {
        this.executor = executor;

        checkArgument(renewTimeoutMs > 0);
        this.renewTimeoutMs = renewTimeoutMs;
    }

    @Override
    public void set(Runnable command) {
        checkNotNull(command);

        executor.scheduleWithFixedDelay(command, renewTimeoutMs, renewTimeoutMs, TimeUnit.MILLISECONDS);
    }
}
