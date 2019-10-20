package net.ndolgov.evbox.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Statistics {
    private final int totalCount;
    private final int startedCount;
    private final int stoppedCount;

    @JsonCreator
    public Statistics(@JsonProperty("startedCount") int startedCount,
                      @JsonProperty("stoppedCount") int stoppedCount) {
        this.startedCount = startedCount;
        this.stoppedCount = stoppedCount;
        this.totalCount = startedCount + stoppedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getStartedCount() {
        return startedCount;
    }

    public int getStoppedCount() {
        return stoppedCount;
    }

    public Statistics incStarted() {
        return new Statistics(startedCount + 1, stoppedCount);
    }

    public Statistics incFinished() {
        return new Statistics(startedCount, stoppedCount + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return totalCount == that.totalCount &&
                startedCount == that.startedCount &&
                stoppedCount == that.stoppedCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCount, startedCount, stoppedCount);
    }

    @Override
    public String toString() {
        return startedCount + "|" + stoppedCount;
    }
}
