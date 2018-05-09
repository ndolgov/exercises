package net.ndolgov.exercise.renewer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sum up all the updates for a particular key
 */
final class AggregatingCallback implements RenewCallback<String, Integer> {
    private final ConcurrentMap<String, AtomicInteger> summedUp;

    AggregatingCallback(String[] keys) {
        summedUp = new ConcurrentHashMap<String, AtomicInteger>(keys.length);
        for (String key : keys) {
            summedUp.put(key, new AtomicInteger());
        }
    }

    public void onUpdate(String key, Integer value) {
        summedUp.get(key).getAndAdd(value);
    }

    public int getAggregated(String key) {
        return summedUp.get(key).get();
    }
}
