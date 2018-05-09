package net.ndolgov;

public interface RangeStorageFactory {
    /**
     * Build an immutable storage optimized for range queries on up to 32K items.
     * The position in the “data” array represents the “id” for that instance in question.
     */
    RangeStorage createStorage(long[] data);
}