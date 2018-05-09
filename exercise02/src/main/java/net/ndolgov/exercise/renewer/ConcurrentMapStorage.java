package net.ndolgov.exercise.renewer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * CHM-back storage implementation
 */
public final class ConcurrentMapStorage<K, V> implements Storage<K, V> {
    private final ConcurrentMap<K, V> map;

    public ConcurrentMapStorage() {
        map = new ConcurrentHashMap<K, V>();
    }

    public ConcurrentMapStorage(ConcurrentMap<K, V> map) {
        this.map = map;
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
