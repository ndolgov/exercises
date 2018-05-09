package net.ndolgov;

/**
 * Storage optimized for range queries
 */
public interface RangeStorage {
    /**
     * @return Ids of all instances that have value between given from and to values with specified inclusivity
     */
    Ids findIdsInRange(long from, long toValue, boolean fromInclusive, boolean toInclusive);
}
