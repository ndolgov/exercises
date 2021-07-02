package net.ndolgov.exercise2;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

public class LruWithTtl {
    public static final class Node<K, V> {
        public Node(K k, V v) {
            this.value = v;
            this.key = k;
            this.createdAt = System.currentTimeMillis();
        }
        public Node(K k, V v, long createdAt) {
            this.value = v;
            this.key = k;
            this.createdAt = createdAt;
        }
        final V value;
        final long createdAt;
        final K key;
        Node<K, V> next;
        Node<K, V> prev;
    }


    public static class Cache<K, V> {
        private final int maxEntries;
        private final long ttlMs;
        private Function<K, V> lookup;

        private Node<K, V> head;
        private Node<K, V> tail;
        private final Map<K, Node<K, V>> index;

        public Cache(int maxEntries, long ttlMs, Function<K, V> lookup) {
            this.maxEntries = maxEntries;
            if (maxEntries < 1) {
                throw new RuntimeException();
            }
            this.ttlMs = ttlMs;
            this.lookup = lookup;
            this.index = new HashMap<>(maxEntries);
        }

        public void clear() {
        }

        public V get(K key) {
            final Node<K, V> existing = index.get(key);
            if (existing == null) {

                if (index.size() >= maxEntries) {
                    final Node<K, V> target = tail;
                    tail = target.prev;
                    target.prev = null;

                    if (tail == null) {
                        head = tail;
                    } else {
                        tail.next = null;
                    }

                    index.remove(target.key);
                    System.out.println("Removed lru'ed: " + target.key);

                    final Node<K, V> newHead = new Node(key, lookup.apply(key));
                    prepend(newHead);
                    index.put(key, newHead);

                    return newHead.value;
                } else {
                    final Node<K, V> newHead = new Node(key, lookup.apply(key));
                    prepend(newHead);
                    index.put(key, newHead);

                    return newHead.value;
                }
            } else {
                final long now = System.currentTimeMillis();
                if (existing.createdAt + ttlMs < now) {
                    remove(existing);
                    index.remove(existing.key);

                    final Node<K, V> newHead = new Node(key, lookup.apply(key));
                    prepend(newHead);
                    index.put(key, newHead);

                    return newHead.value;
                } else {
                    remove(existing);
                    index.remove(existing.key);

                    final Node<K, V> newHead = new Node(key, existing.value, existing.createdAt);
                    prepend(newHead);
                    index.put(key, newHead);

                    return newHead.value;

                }
            }


        }

        private void prepend(Node<K, V> newHead) {
            final Node<K, V> oldHead = head;
            head = newHead;
            newHead.next = oldHead;
            if (oldHead != null) {
                oldHead.prev = newHead;
            } else {
                tail = newHead;
            }

        }

        private void remove (Node<K, V> target) {
            //final Node<K, Pair<V>> target = tail;
            //tail = target.prev;
            if (target.prev != null) {
                target.prev.next = target.next;
            }
            if (target.next != null) {
                target.next.prev = target.prev;
            }
            target.prev = null;
            target.next = null;

            // if (tail == null) {
            //     head = tail;
            // } else {
            //     tail.next = null
            // }

            //index.remove(target.key);
        }

    }

    public static void main(String [] args) {
        TestData<String, String> testData = new TestData<>();
        testData.put("Fred", "Anderson");
        testData.put("Sally", "Johnson");

        Cache cache = new Cache(3, 1000, (k) -> testData.get(k));
        System.out.println(cache.get("Fred"));
        System.out.println(cache.get("Sally"));
        System.out.println(cache.get("Missing"));
        System.out.println(cache.get("1"));
        System.out.println(cache.get("2"));
        System.out.println(cache.get("3"));
    }

    public static void main2(String [] args) {
        // Setup test data so we can track when lookups from
        // source data are made
        TestData<String, String> testData = new TestData<>();
        testData.put("Fred", "Anderson");
        testData.put("Sally", "Johnson");

        // Create the cache to test
        Cache cache = new Cache(10, 1000, (k) -> testData.get(k));
        testData.resetCalled();
        // Getting each value and a missing value the first time
        // should each cause a lookup from the source data
        assertTrue(cache.get("Fred").equals("Anderson"));
        assertTrue(testData.lookupCalled("Fred"));
        assertTrue(cache.get("Sally").equals("Johnson"));
        assertTrue(testData.lookupCalled("Sally"));
        assertTrue(cache.get("Missing") == null);
        assertTrue(testData.lookupCalled("Missing"));
        testData.resetCalled();

        // Looking up data already in the cache, including null for missing
        // should not incur a lookup from source data
        assertTrue(cache.get("Fred").equals("Anderson"));
        assertFalse(testData.lookupCalled("Fred"));
        assertTrue(cache.get("Missing") == null);
        assertFalse(testData.lookupCalled("Missing"));
        testData.resetCalled();

        // At this point Sally is the least recently accessed
        // so if we add 8 entries, sally should fall out of cache
        // due to the cache exceeding its max size
        for(int i = 0; i < 8; i++) {
            cache.get(Integer.toString(i));
        }

        // Getting Sally should now cause a lookup but fred and missing should not
        assertTrue(cache.get("Fred").equals("Anderson"));
        assertFalse(testData.lookupCalled("Fred"));
        assertTrue(cache.get("Missing") == null);
        assertFalse(testData.lookupCalled("Missing"));
        assertTrue(cache.get("Sally").equals("Johnson"));
        assertTrue(testData.lookupCalled("Sally"));

        // Getting expired entries that do exist in the cache should
        // cause a lookup from the source data
        // Sleep to time out entries
        try { TimeUnit.SECONDS.sleep(1); } catch(Exception ignore) {}
        // Getting Fred, Sally or Missing should now cause a lookup
        testData.resetCalled();
        assertTrue(cache.get("Fred").equals("Anderson"));
        assertTrue(testData.lookupCalled("Fred"));
        assertTrue(cache.get("Sally").equals("Johnson"));
        assertTrue(testData.lookupCalled("Sally"));
        assertTrue(cache.get("Missing") == null);
        assertTrue(testData.lookupCalled("Missing"));
    }

    // Test helper functions
    public static void assertTrue(boolean condition) {
        if(!condition)
            throw new RuntimeException("Failed Assertion: Should have been TRUE");
    }

    public static void assertFalse(boolean condition) {
        if(condition)
            throw new RuntimeException("Failed Assertion: Should have been FALSE");
    }

    // Test Data class holds test data and tracks when it is looked up by the cache
    public static class TestData<K, V> extends HashMap<K,V> {
        private final Map<Object, Boolean> lookupMap = new HashMap<>();
        @Override
        public V get(Object key) {
            System.out.println("looking up " + key);
            lookupMap.put(key, true);
            return super.get(key);
        }

        public boolean lookupCalled(K key) {
            return lookupMap.containsKey(key);
        }

        public void resetCalled() {
            lookupMap.clear();
        }
    }
}
