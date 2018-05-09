package net.ndolgov.v2;

import net.ndolgov.Ids;
import net.ndolgov.IdsImpl;
import net.ndolgov.RangeStorage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Assumptions: long -> many unique values -> RLE is unhelpful
 */
final class RangeContainerImpl implements RangeStorage {
    private final Map<Long, Short> valueToPosCache;
    private final long[] data;
    private final short[] index;
    private final IntervalIndex intervalIndex;

    public RangeContainerImpl(long[] data, short[] index, IntervalIndex intervalIndex) {
        this.data = data;
        this.index = index;
        this.intervalIndex = intervalIndex;
        valueToPosCache = new LinkedHashMapCache<Long, Short>(10);
    }

    @Override
    public Ids findIdsInRange(long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
        // todo check with inclusivity
        if (fromValue < data[index[0]])
            return IdsImpl.Empty.INSTANCE;

        if (toValue > data[index[data.length - 1]])
            return IdsImpl.Empty.INSTANCE;

        final int from = adjustFrom(position(fromValue), fromInclusive);
        final int to = adjustTo(position(toValue), toInclusive);

        return (from > to) ? IdsImpl.Empty.INSTANCE : new IdsImpl(from, to, index);
    }

    private int adjustTo(int toPos, boolean toInclusive) {
        if (toPos >= 0) {
            if (toInclusive)
                return toPos;

            int adjusted = toPos;
            final long toValue = data[index[toPos]];
            do {
                adjusted--;
            } while (toValue == data[index[adjusted]]);

            return adjusted;
        } else {
            return -toPos; // excluded regardless because the value itself is missing
        }
    }

    private int adjustFrom(int fromPos, boolean fromInclusive) {
        if (fromPos >= 0) {
            if (fromInclusive)
                return fromPos;

            int adjusted = fromPos;
            final long fromValue = data[index[fromPos]];
            do {
                adjusted++;
            } while (fromValue == data[index[adjusted]]);

            return adjusted;
        } else {
            return -fromPos; // excluded regardless because the value itself is missing
        }
    }

    private int position(long value) {
        final Short cached = valueToPosCache.get(value);
        if (cached != null)
            return cached;

        final int exactPos = search(value);
        if (exactPos >= 0) { // the value is actually present
            valueToPosCache.put(value, (short) exactPos);
        }

        return exactPos;
    }

    // if we get that far, the value (or the corresponding position ) belongs to one of the intervals
    private int search(long value) {
        final IntervalIndex.IntervalNode interval = intervalIndex.root.search(value);
        if (interval == null) {
            throw new IllegalStateException("No interval found for value: " + value + " in index tree for: " + intervalIndex.root.toString());
        }

        return binarySearch(interval.lIndex, interval.rIndex, value);
    }

    // @see Arrays#binarySearch
    // todo adjust for duplicated values
    private int binarySearch(int lIndex, int rIndex, long value) {
        int left = lIndex;
        int right = rIndex - 1;

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
        return -(left + 1);  // value not found.
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
