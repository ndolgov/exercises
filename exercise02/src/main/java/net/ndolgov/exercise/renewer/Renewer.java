package net.ndolgov.exercise.renewer;

public interface Renewer<K, V> {
    void update(K key, V value);
}
