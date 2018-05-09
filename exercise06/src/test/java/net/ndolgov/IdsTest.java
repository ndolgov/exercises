package net.ndolgov;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdsTest {
    @Test
    public void testManyItems() {
        final Ids ids = new IdsImpl(1, 3, index());
        RangeStorageTest.assertSequence(ids, new short[] {500, 501, 502, -1});
    }

    @Test
    public void testOneItem() {
        final Ids ids = new IdsImpl(3, 3, index());
        RangeStorageTest.assertSequence(ids, new short[] {500, -1});
    }

    @Test
    public void testNoItems() {
        final Ids ids = new IdsImpl(2, 1, index());
        assertEquals(-1, ids.nextId());

        assertEquals(-1, IdsImpl.Empty.INSTANCE.nextId());
    }

    private short[] index() {
        return new short[]{503, 502, 501, 500, 499, 498};
    }
}
