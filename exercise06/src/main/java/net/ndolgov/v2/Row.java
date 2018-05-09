package net.ndolgov.v2;

import java.util.Iterator;

/**
 *
 */
class Row {
    final long id;
    final String dept;

    Row(long id, String dept) {
        this.id = id;
        this.dept = dept;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (id != row.id) return false;
        if (!dept.equals(row.dept)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + dept.hashCode();
        return result;
    }
}

/*
public void joinSortedStreams(Iterator<Row> t1, Iterator<Row> t2) {
    Row lCursor = t1.hasNext() ? t1.next() : null;
    Row rCursor = t2.hasNext() ? t2.next() : null;

    if (lCursor == null)
        return rCursor == null ? emptyResultset() : rightOnly();

    if (rCursor == null)
        return lCursor == null ? emptyResultset() : leftOnly();

    boolean isRunning = true;
    while (isRunning) {
        while (rCursor != null && lCursor.id > rCursor.id) {
            emitLeftOnly(lCursor);
            rCursor = t2.hasNext() ? t2.next() : null;
        }

        while (rCursor != null && lCursor.id == rCursor.id) {
            emitJoined(lCursor, rCursor);
            rCursor = t2.hasNext() ? t2.next() : null;
        }

        lCursor = t1.hasNext() ? t1.next() : null;
    }
}
*/
