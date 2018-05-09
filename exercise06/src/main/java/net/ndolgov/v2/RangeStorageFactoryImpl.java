package net.ndolgov.v2;

import net.ndolgov.RangeStorage;
import net.ndolgov.RangeStorageFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 */
public final class RangeStorageFactoryImpl implements RangeStorageFactory {
    private final int powerOfTwo;

    public RangeStorageFactoryImpl(int powerOfTwo) {
        this.powerOfTwo = powerOfTwo;
    }

    @Override
    public RangeStorage createStorage(long[] data) {
        final IntervalIndex intervalIndex = new IntervalIndex(0, data.length - 1, powerOfTwo, data);
        return new RangeContainerImpl(data, sort(data), intervalIndex);
    }

    // todo replace with a real log(N) sort algo
    private static short[] sort(long[] data) {
        final short[] index = new short[data.length];

        final Map<Long, Short> sorted = new TreeMap<Long, Short>();
        for (int i = 0; i < data.length; i++) {
            sorted.put(data[i], (short) i);
        }

        int i = 0;
        for (Short ptr : sorted.values())
            index[i++] = ptr;

        return index;
    }
 }
