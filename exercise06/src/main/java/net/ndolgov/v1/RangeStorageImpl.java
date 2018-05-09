package net.ndolgov.v1;

import net.ndolgov.Ids;
import net.ndolgov.IdsImpl;
import net.ndolgov.RangeStorage;

import java.util.LinkedHashMap;
import java.util.Map;

final class RangeStorageImpl implements RangeStorage {
    private static enum DIRECTION {TO_LEFT, TO_RIGHT}

    private final Map<Long, Short> valueToPosCache; // todo replace with gnu.trove.map.hash.TLongShortHashMap + expiration?

    private final long[] data;

    private final short[] index;

    public RangeStorageImpl(long[] data, short[] index) {
        this.data = data;
        this.index = index;
        valueToPosCache = new LinkedHashMapCache<Long, Short>(10);
    }

    @Override
    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
        if (fromValue > toValue)
            return IdsImpl.Empty.INSTANCE;

        if (toValue < data[index[0]])
            return IdsImpl.Empty.INSTANCE;

        if (fromValue > data[index[data.length - 1]])
            return IdsImpl.Empty.INSTANCE;

        final int from = from(fromValue, fromInclusive);
        final int to = to(toValue, toInclusive, from);

        return isRangeEmpty(from, to) ? IdsImpl.Empty.INSTANCE : new IdsImpl(from, to, index);
    }

    private int to(long toValue, boolean toInclusive, int from) {
        final int exactTo = exactPosition(toValue, from, DIRECTION.TO_LEFT);

        if (exactTo == Integer.MAX_VALUE) { // upper boundary not in the data array
            return adjustToMissing(-(data.length - 1), toValue);
        } else {
            return (exactTo < 0) ? adjustToMissing(exactTo, toValue) : adjustTo(exactTo, toInclusive);
        }
    }

    private int from(long fromValue, boolean fromInclusive) {
        final int exactFrom = exactPosition(fromValue, 0, DIRECTION.TO_RIGHT);

        if (exactFrom == Integer.MIN_VALUE) { // lower boundary not in the data array
            return adjustFromMissing(0, fromValue);
        } else {
            return  (exactFrom < 0) ? adjustFromMissing(exactFrom, fromValue) : adjustFrom(exactFrom, fromInclusive);
        }
    }

    private boolean isRangeEmpty(int from, int to) {
        return (from >= data.length) || (to <= -1) || (from > to);
    }

    /**
     * Search in subarray data[from, data.length - 1]
     * @param value target value
     * @param from the left boundary, an optimization to skip already scanned subarray
     * @return the exact position of any value instance found (to be adjusted) or -1 if there is no such value
     */
    private int exactPosition(long value, int from, DIRECTION direction) {
        // 0.
        if (from >= data.length) {
            return Integer.MAX_VALUE; // search for initial position reached EOF already so resultset will be empty regardless
        }

        // 1. partially overlapping intervals on the right boundary
        final long firstValue = data[index[from]];
        if (value < firstValue) {
            if (direction == DIRECTION.TO_RIGHT) {
                return Integer.MIN_VALUE;
            } else {
                throw new IllegalStateException("Trying to go to the left of left boundary with value: " + value);
            }
        }

        // 2. partially overlapping intervals on the left boundary
        final long lastValue = data[index[index.length - 1]];
        if (value > lastValue) {
            if (direction == DIRECTION.TO_LEFT) {
                return Integer.MAX_VALUE;
            } else {
                throw new IllegalStateException("Trying to go to the right of right boundary with value: " + value);
            }
        }

        // 3. completely nested intervals

        // 3.1 exact boundary short path
        if (value == firstValue) {
            return from;
        }

        if (value == lastValue) {
            return index.length - 1;
        }

        return searchPosition(value, from);
    }

    private int searchPosition(long value, int from) {
        final Short cached = valueToPosCache.get(value);
        if (cached != null) {
            return cached;
        }

        final int exactPos = binarySearch(from, data.length - 1, value);
        if (exactPos >= 0) { // the value is actually present
            valueToPosCache.put(value, (short) exactPos);
        }

        return exactPos;
    }

    private int adjustFrom(int exactFrom, boolean fromInclusive) {
        final long value = data[index[exactFrom]];

        if (fromInclusive) { // include repeated values to the left of the exact position
            int adjusted = exactFrom;
            while (adjusted > 0) {
                if (value == data[index[adjusted - 1]]) {
                    adjusted--;
                } else {
                    return adjusted;
                }
            }

            return adjusted;
        } else { // exclude repeated values to the right of the exact position
            int adjusted = exactFrom;
            while (adjusted < index.length - 1) {
               if (value == data[index[adjusted + 1]]) {
                   adjusted++;
               } else {
                   return adjusted + 1;
               }
            }

            return index.length; // EOF so that resultset is empty
        }
    }

    private int adjustFromMissing(int exactFrom, long value) {
        int assumed = -exactFrom; // the value itself is missing but this is where it would be otherwise
        while (assumed < data.length) {
            if (data[index[assumed]] < value) {
                assumed++;
            } else {
                return assumed;
            }
        }

        return index.length; // EOF, resultset is empty
    }

    private int adjustTo(int exactTo, boolean toInclusive) {
        final long value = data[index[exactTo]];

        if (toInclusive) { // include repeated values to the right of the exact position
            int adjusted = exactTo;
            while (adjusted < index.length - 1) {
                if (value == data[index[adjusted + 1]]) {
                    adjusted++;
                } else {
                    return adjusted;
                }
            }

            return adjusted; // index.length-1
        } else { // exclude repeated values to the left of the exact position
            int adjusted = exactTo;
            while (adjusted >= 0) {
                if (value == data[index[adjusted]]) {
                    adjusted--;
                } else {
                    return adjusted;
                }
            }

            return adjusted; // -1, empty resultset
        }
    }

    private int adjustToMissing(int exactTo, long value) {
        int assumed = -exactTo; // the value itself is missing but this is where it would be otherwise
        while (assumed >= 0) {
            if (data[index[assumed]] > value) {
                assumed--;
            } else {
                return assumed;
            }
        }

        return assumed;
    }

    // @see Arrays#binarySearch
    private int binarySearch(int from, int to, long value) {
        int left = from;
        int right = to;

        while (left <= right) {
            final int mid = (left + right) >>> 1;
            final long midVal = data[index[mid]];

            if (midVal < value)
                left = mid + 1;
            else if (midVal > value)
                right = mid - 1;
            else
                return mid; // value found
        }

        return -left;  // value not found; return the position into which it could be inserted (e.g. "-1" for "4 8 9" and "5")
    }

    /**
     * LRU cache of the last few value-to-position pair resolved
     */
    private static final class LinkedHashMapCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        public LinkedHashMapCache(int maxSize) {
            this.maxSize = maxSize;
        }

        protected final boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }
    }
}
