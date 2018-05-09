package net.ndolgov;

public interface Ids {
    static final short END_OF_IDS = -1;

    /**
     * return the next id in a ASC-sorted sequence, -­1 if at end of data.
     */
    short nextId();
}