package net.ndolgov.v1;

import net.ndolgov.RangeStorageFactory;
import net.ndolgov.RangeStorage;

import java.util.*;

public final class RangeStorageFactoryImpl implements RangeStorageFactory {
    @Override
    public RangeStorage createStorage(long[] data) {
        return new RangeStorageImpl(data, sortedIndex(data));
    }

    /**
     * todo replace with a real stable log(N) sort algo
     * The sorting algo <b>must be STABLE</b>
     * @param data the original data
     * @return an array of pointers to the original data that allows to index the sorted order without reshuffling the original data
     */
    private static short[] sortedIndex(long[] data) {
        final short[] index = new short[data.length];

        final Map<Long, Set<Short>> sorted = new TreeMap<Long, Set<Short>>();
        for (int i = 0; i < data.length; i++) {
            final Set<Short> existing = sorted.get(data[i]);
            if (existing == null) {
                final Set<Short> created = new LinkedHashSet<>(10);
                created.add((short) i);
                sorted.put(data[i], created);
            } else {
                existing.add((short) i);
            }
        }

        int i = 0;
        for (Set<Short> set : sorted.values()) {
            for (Short idx : set) {
                index[i++] = idx;
            }
        }

        return index;
    }
 }
