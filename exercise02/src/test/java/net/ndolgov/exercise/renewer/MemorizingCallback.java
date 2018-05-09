package net.ndolgov.exercise.renewer;

/**
 * Remember the last received (key, value) pair
 */
final class MemorizingCallback<K, V> implements RenewCallback<K, V> {
    volatile K key;
    volatile V value;
    volatile int counter; // potentially missed concurrent updates are of no concern here so no need for atomics

    public void onUpdate(K key, V value) {
        this.key = key;
        this.value = value;
        this.counter++;
    }
}
