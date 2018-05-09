package net.ndolgov;

import net.ndolgov.v1.RangeStorageFactoryImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public final class RangeStorageTest {

    @Test
    public void testUniqueValues() {
        // (10,11,12) -> {0, 7, 4}
        final RangeStorage container = container(new long[]{10, 4, 9, 100, 12, 20, 99, 11, 15});
        final Ids inclusive = container.findIdsInRange(10, 12, true, true);
        assertSequence(inclusive, new short[] {0, 4, 7, -1});

        final Ids exclusive = container.findIdsInRange(10, 12, false, false);
        assertSequence(exclusive, new short[] {7, -1});

        final Ids rexclusive = container.findIdsInRange(10, 12, true, false);
        assertSequence(rexclusive, new short[] {0, 7, -1});

        final Ids lexclusive = container.findIdsInRange(10, 12, false, true);
        assertSequence(lexclusive, new short[] {4, 7, -1});
    }

    @Test
    public void testOutOfRange() {
        final RangeStorage container = container(new long[]{10, 4, 9, 100, 12, 20, 10, 99, 9, 11, 15});

        final Ids toTheLeft = container.findIdsInRange(1, 2, true, true);
        assertEquals(-1, toTheLeft.nextId());

        final Ids toTheRight = container.findIdsInRange(101, 102, true, true);
        assertEquals(-1, toTheRight.nextId());
    }

    @Test
    public void testPartiallyOverlapping() {
        final RangeStorage container = container(new long[] {10, 4, 9, 100, 12, 20, 10, 99, 9, 11, 15});

        final Ids toTheLeft = container.findIdsInRange(1, 5, true, true);
        assertSequence(toTheLeft, new short[] {1, -1});

        final Ids toTheRight = container.findIdsInRange(99, 102, true, true);
        assertSequence(toTheRight, new short[] {3, 7, -1});
    }

    @Test
    public void testOverlappingOnTheLeft() {
        final RangeStorage container = container(new long[] {1, 1, 3, 3, 5, 5, 7, 7});

        final Ids range0i1i = container.findIdsInRange(0, 1, true, true);
        assertSequence(range0i1i, new short[] {0, 1, -1});

        final Ids range0e1i = container.findIdsInRange(0, 1, false, true);
        assertSequence(range0e1i, new short[] {0, 1, -1});

        final Ids range0i1e = container.findIdsInRange(0, 1, true, false);
        assertEquals(-1, range0i1e.nextId());

        final Ids range0e1e = container.findIdsInRange(0, 1, false, false);
        assertEquals(-1, range0e1e.nextId());
    }

    @Test
    public void testOverlappingOnTheRight() {
        final RangeStorage container = container(new long[] {1, 1, 3, 3, 5, 5, 7, 7});

        final Ids range7i9i = container.findIdsInRange(7, 9, true, true);
        assertSequence(range7i9i, new short[] {6, 7, -1});

        final Ids range7e9i = container.findIdsInRange(7, 9, false, true);
        assertEquals(-1, range7e9i.nextId());

        final Ids range7i9e = container.findIdsInRange(7, 9, true, false);
        assertSequence(range7i9e, new short[] {6, 7, -1});

        final Ids range7e9e = container.findIdsInRange(7, 9, false, false);
        assertEquals(-1, range7e9e.nextId());
    }

    @Test
    public void testEntire() {
        final RangeStorage container = container(new long[] {1, 1, 3, 3, 5, 5, 7, 7});

        final Ids range1i7i = container.findIdsInRange(1, 7, true, true);
        assertSequence(range1i7i, new short[]{0, 1, 2, 3, 4, 5, 6, 7, -1});

        final Ids range1e7e = container.findIdsInRange(1, 7, false, false);
        assertSequence(range1e7e, new short[] {2,3,4,5,-1});

        final Ids range0i8i = container.findIdsInRange(0, 8, true, true);
        assertSequence(range0i8i, new short[]{0, 1, 2, 3, 4, 5, 6, 7, -1});

        final Ids range0e8e = container.findIdsInRange(0, 8, false, false);
        assertSequence(range0e8e, new short[] {0,1,2,3,4,5,6,7,-1});
    }

    @Test
    public void testAllTheSameValues() {
        final RangeStorage container = container(new long[] {5, 5, 5, 5, 5, 5, 5, 5});

        final short[] allExpectedIds = {0, 1, 2, 3, 4, 5, 6, 7, -1};
        final short[] empty = {-1};

        final Ids range5i5i = container.findIdsInRange(5, 5, true, true);
        assertSequence(range5i5i, allExpectedIds);

        final Ids range5i5e = container.findIdsInRange(5, 5, true, false);
        assertSequence(range5i5e, empty);

        final Ids range5e5i = container.findIdsInRange(5, 5, false, true);
        assertSequence(range5e5i, empty);

        final Ids range5e5e = container.findIdsInRange(5, 5, false, false);
        assertSequence(range5e5e, empty);

        final Ids range4i6i = container.findIdsInRange(4, 6, true, true);
        assertSequence(range4i6i, allExpectedIds);

        final Ids range4i6e = container.findIdsInRange(4, 6, true, false);
        assertSequence(range4i6e, allExpectedIds);

        final Ids range4e6i = container.findIdsInRange(4, 6, false, true);
        assertSequence(range4e6i, allExpectedIds);

        final Ids range4e6e = container.findIdsInRange(4, 6, false, false);
        assertSequence(range4e6e, allExpectedIds);
    }

    static void assertSequence(Ids ids, short[] sequence) {
        for (short expectedId : sequence) {
            assertEquals(expectedId, ids.nextId());
        }
    }

    private static RangeStorage container(long[] data) {
        return new RangeStorageFactoryImpl().createStorage(data);
    }
}
