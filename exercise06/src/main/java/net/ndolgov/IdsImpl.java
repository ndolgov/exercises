package net.ndolgov;

import java.util.Arrays;

public final class IdsImpl implements Ids {
    private final short[] array;

    private int cursor = 0;

    public IdsImpl(int from, int to, short[] index) {
        array = new short[to - from + 1];
        for (int i = from, pos = 0; i <= to; i++, pos++) {
            array[pos] = index[i];
        }

        Arrays.sort(array);
    }

    @Override
    public short nextId() {
        if (cursor < array.length) {
            final short value = array[cursor];
            cursor++;
            return value;
        } else {
            return END_OF_IDS;
        }
    }

    public final static class Empty implements Ids {
        public final static Empty INSTANCE = new Empty();

        @Override
        public short nextId() {
            return END_OF_IDS;
        }
    }

    @Override
    public String toString() {
        return "{IdsImpl:cursor=" + cursor + "}";
    }
}
