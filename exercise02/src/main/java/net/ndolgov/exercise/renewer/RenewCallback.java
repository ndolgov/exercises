package net.ndolgov.exercise.renewer;

public interface RenewCallback<K,V> {
    void onUpdate(K key, V value);
}
