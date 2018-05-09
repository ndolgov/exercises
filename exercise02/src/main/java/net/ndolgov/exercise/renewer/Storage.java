package net.ndolgov.exercise.renewer;

/**
 * The key-value storage abstraction that could be backed by anything from CHM to an Infinispan-style distributed cache.
 * The method have the same semantics as the corresponding methods of {@link java.util.Map}
 */
public interface Storage<K, V> {
    V put(K key, V value);

    V get(K key);

    V remove(K key);

    boolean containsKey(K key);
}
