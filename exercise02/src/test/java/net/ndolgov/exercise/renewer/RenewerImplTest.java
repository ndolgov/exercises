package net.ndolgov.exercise.renewer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class RenewerImplTest {
    private static final String KEY1 = "K1";
    private static final String VALUE1 = "V1";
    private static final int PERIOD = 500;
    private static final int ONE_TIME = 1;
    private static final int THREE_TIMES = 3;
    private static final int FOUR_TIMES = 6;

    /** The last exception thrown from a test thread, to be checked after each concurrent test */
    static volatile Exception exception;

    @Before
    public void beforeTest() {
        exception = null;
    }

    @After
    public void afterTest() {
        if (exception != null) {
            exception.printStackTrace();
            fail("Exception caught in a child thread: " + exception.getMessage());
        }
    }

    @Test
    public void testImmediateCallbackUpdate() {
        final MemorizingCallback<String, Object> callback = new MemorizingCallback<String, Object>();
        final ConcurrentMapStorage<String, Object> storage = new ConcurrentMapStorage<String, Object>();
        final ScheduledExecutorTimer timer = new ScheduledExecutorTimer(PERIOD);

        final Renewer<String, Object> renewer = new RenewerImpl<String, Object>(storage, timer, callback);
        renewer.update(KEY1, VALUE1);

        assertEquals(VALUE1, storage.get(KEY1));

        assertEquals(KEY1, callback.key);
        assertEquals(VALUE1, callback.value);
        assertEquals(ONE_TIME, callback.counter);
    }

    @Test
    public void testTimeoutTriggeredCallbackUpdate() throws Exception {
        final MemorizingCallback<String, Object> callback = new MemorizingCallback<String, Object>();
        final Storage<String, Object> storage = new ConcurrentMapStorage<String, Object>();
        final Timer timer = new ScheduledExecutorTimer(PERIOD);

        final Renewer<String, Object> renewer = new RenewerImpl<String, Object>(storage, timer, callback);
        renewer.update(KEY1, VALUE1);

        assertEquals(KEY1, callback.key);
        assertEquals(VALUE1, callback.value);
        assertEquals(ONE_TIME, callback.counter);

        Thread.sleep(PERIOD * THREE_TIMES);  // at least two timer-triggered updates are expected in the meantime

        assertEquals(KEY1, callback.key);
        assertEquals(VALUE1, callback.value);
        assertTrue(callback.counter >= THREE_TIMES);
    }

    @Test
    public void testConcurrentUpdates() throws Exception {
        final String[] keys = new String[] {"K0", "K1", "K2", "K3", "K4", "K5", "K6", "K7", "K8", "K9"};

        final AggregatingCallback callback = new AggregatingCallback(keys);
        final Storage<String, Integer> storage = new ConcurrentMapStorage<String, Integer>();
        final Timer timer = new ScheduledExecutorTimer(600000); // never
        final Renewer<String, Integer> renewer = new RenewerImpl<String, Integer>(storage, timer, callback);

        final Updater[] updaters = new Updater[keys.length];
        final int repetitions = 50000;
        final CyclicBarrier barrier = new CyclicBarrier(updaters.length + 1);

        final ExecutorService executor = Executors.newFixedThreadPool(updaters.length + 1);
        try {
            for (int i = 0; i < updaters.length; i++) {
                updaters[i] = new Updater(renewer, repetitions, barrier, keys);
                executor.execute(updaters[i]);
            }

            barrier.await(10, TimeUnit.SECONDS); // start

            barrier.await(1, TimeUnit.MINUTES); // wait for completion

        } finally {
            executor.shutdownNow();
        }

        // for each key, calculate the total sum and compare it with the value aggregated by the callback
        for (String key : keys) {
            int value = 0;

            for (Updater updater : updaters) {
                value += updater.getAggregated(key);
            }

            assertEquals(value, callback.getAggregated(key));
        }

    }

    @Test
    public void testBrokenCallback() {
        final RenewCallback<String, Object> callback = new RenewCallback<String, Object>() {
            public void onUpdate(String key, Object value) {
                throw new RuntimeException("Simulating callback failure");
            }
        };

        final Renewer<String, Object> renewer = new RenewerImpl<String, Object>(new ConcurrentMapStorage<String, Object>(), new ScheduledExecutorTimer(PERIOD), callback);
        renewer.update(KEY1, VALUE1);
    }

    @Test (expected = NullPointerException.class)
    public void testMissingKey() {
        final MemorizingCallback<String, Object> callback = new MemorizingCallback<String, Object>();
        final ConcurrentMapStorage<String, Object> storage = new ConcurrentMapStorage<String, Object>();
        final ScheduledExecutorTimer timer = new ScheduledExecutorTimer(PERIOD);

        final Renewer<String, Object> renewer = new RenewerImpl<String, Object>(storage, timer, callback);
        renewer.update(null, VALUE1);
    }

    @Test
    public void testOnlyOneTimerPerKey() throws Exception {
        final MemorizingCallback<String, Object> callback = new MemorizingCallback<String, Object>();
        final ConcurrentMapStorage<String, Object> storage = new ConcurrentMapStorage<String, Object>();
        final ScheduledExecutorTimer timer = new ScheduledExecutorTimer(PERIOD);

        final Renewer<String, Object> renewer = new RenewerImpl<String, Object>(storage, timer, callback);
        final int repetitions = 5;
        for (int i = 0; i < repetitions; i++) {
            renewer.update(KEY1, VALUE1);
        }

        Thread.sleep(PERIOD * THREE_TIMES);  // at least two timer-triggered updates are expected in the meantime

        assertEquals(KEY1, callback.key);
        assertEquals(VALUE1, callback.value);
        assertTrue(callback.counter > repetitions); // at least one timer-triggered update
        assertTrue(callback.counter <= THREE_TIMES + repetitions); // give some leeway but small enough to catch multiple timers
    }
}
