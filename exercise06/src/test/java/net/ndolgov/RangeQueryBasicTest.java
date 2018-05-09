package net.ndolgov;

import org.junit.Test;

import static org.junit.Assert.*;

public class RangeQueryBasicTest {
    private final long[] data = {10, 12, 17, 21, 2, 15, 16};

    @Test
    public void runRangeQuery() {
        final RangeStorage storageV1 = new net.ndolgov.v1.RangeStorageFactoryImpl().createStorage(data);
        assertRangeQuery(storageV1);

        final RangeStorage storageV2 = new net.ndolgov.v2.RangeStorageFactoryImpl(1).createStorage(data);;
        //assertRangeQuery(storageV2);
    }

    private static void assertRangeQuery(RangeStorage storage) {
        Ids ids = storage.findIdsInRange(14, 17, true, true);
        assertEquals(2, ids.nextId());
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
        ids = storage.findIdsInRange(14, 17, true, false);
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
        ids = storage.findIdsInRange(20, Long.MAX_VALUE, false, true);
        assertEquals(3, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }
}