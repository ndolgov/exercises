package net.ndolgov.fourtytwo.queryserver.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A single row of execute query request results. It assumes a static schema for simplicity sake.
 */

public class ResultRow {
    private final int hour;
    private final int device;
    private final int metric;
    private final double min;
    private final double max;
    private final double avg;
    private final long count;

    @JsonCreator
    public ResultRow(@JsonProperty("hour") int hour,
                     @JsonProperty("device") int device,
                     @JsonProperty("metric") int metric,
                     @JsonProperty("min") double min,
                     @JsonProperty("max") double max,
                     @JsonProperty("avg") double avg,
                     @JsonProperty("count") long count) {
        this.hour = hour;
        this.device = device;
        this.metric = metric;
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.count = count;
    }

    public int getHour() {
        return hour;
    }

    public int getDevice() {
        return device;
    }

    public int getMetric() {
        return metric;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAvg() {
        return avg;
    }

    public long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultRow resultRow = (ResultRow) o;
        return hour == resultRow.hour &&
                device == resultRow.device &&
                metric == resultRow.metric &&
                Double.compare(resultRow.min, min) == 0 &&
                Double.compare(resultRow.max, max) == 0 &&
                Double.compare(resultRow.avg, avg) == 0 &&
                count == resultRow.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, device, metric, min, max, avg, count);
    }

    @Override
    public String toString() {
        return "ResultRow{" +
                "hour=" + hour +
                ", device=" + device +
                ", metric=" + metric +
                ", min=" + min +
                ", max=" + max +
                ", avg=" + avg +
                ", count=" + count +
                '}';
    }
}
