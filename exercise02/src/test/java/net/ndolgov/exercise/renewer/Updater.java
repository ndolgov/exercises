package net.ndolgov.exercise.renewer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Make a given number of update repetitions for each of the randomly shuffled keys
 */
final class Updater implements Runnable {
    private final Renewer<String, Integer> renewer;
    private final int repetitions;
    private final CyclicBarrier barrier;
    private final String[] keys;
    private final Map<String, Integer> summedUp;

    public Updater(Renewer<String, Integer> renewer, int repetitions, CyclicBarrier barrier, String[] keys) {
        this.renewer = renewer;
        this.repetitions = repetitions;
        this.barrier = barrier;

        final List<String> shuffledKeys = Lists.newArrayList(keys);
        Collections.shuffle(shuffledKeys);
        this.keys = shuffledKeys.toArray(new String[shuffledKeys.size()]);

        summedUp = Maps.newHashMapWithExpectedSize(keys.length);
        for (String key : keys) {
            summedUp.put(key, 0);
        }
    }

    @Override
    public void run() {
        try {
            barrier.await(10, TimeUnit.SECONDS);

            int value = (this.hashCode() ^ (int)System.nanoTime());

            for (int i = 0; i < repetitions; i++) {
                for (int j = 0; j < keys.length; j++) {
                    final String key = keys[j];
                    renewer.update(key, value);
                    summedUp.put(key, summedUp.get(key) + value);
                }

                value = xorShift(value);
            }

            barrier.await(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            RenewerImplTest.exception = e;
        }
    }

    public int getAggregated(String key) {
        return summedUp.get(key);
    }

    private static int xorShift(int i) {
        i ^= (i << 6);
        i ^= (i >>> 21);
        i ^= (i << 7);
        return i;
    }
}
