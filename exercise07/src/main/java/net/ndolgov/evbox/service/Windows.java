package net.ndolgov.evbox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class Windows {
    public interface WindowStats<T> {
        void onChange(UnaryOperator<T> updater);

        void onPeriodEnd();

        T stats();
    }

    public static <T> WindowStats<T> tumblingWindow(T initialValue, long periodSecs, ScheduledExecutorService executor) {
        final TumblingWindowStats<T> window = new TumblingWindowStats<>(initialValue);

        executor.scheduleAtFixedRate(window::onPeriodEnd, periodSecs, periodSecs, TimeUnit.SECONDS);

        return window;
    }

    private static final class TumblingWindowStats<T> implements WindowStats<T> {
        private final Logger log = LoggerFactory.getLogger(WindowStats.class);

        private final AtomicReference<T> state;

        private final T initialValue;

        public TumblingWindowStats(T initialValue) {
            this.initialValue = initialValue;
            this.state = new AtomicReference<>(initialValue);
        }

        @Override
        public void onChange(UnaryOperator<T> updater) {
            state.updateAndGet(updater);
            log.info("Updated stats " + stats());
        }

        @Override
        public void onPeriodEnd() {
            state.set(initialValue);
            log.info("Reset stats");
        }

        @Override
        public T stats() {
            return state.get();
        }
    }

    private Windows() {
    }
}
