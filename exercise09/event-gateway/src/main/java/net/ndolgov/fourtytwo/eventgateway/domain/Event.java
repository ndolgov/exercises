package net.ndolgov.fourtytwo.eventgateway.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * IoT event as a JSON snippet
 */
public class Event {
    private final int device;
    private final int metric;
    private final long timestamp;
    private final double value;

    @JsonCreator
    public Event(@JsonProperty("device") int device,
                 @JsonProperty("metric") int metric,
                 @JsonProperty("timestamp") long timestamp,
                 @JsonProperty("value") double value) {
        this.device = device;
        this.metric = metric;
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getDevice() {
        return device;
    }

    public int getMetric() {
        return metric;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return device == event.device &&
                metric == event.metric &&
                timestamp == event.timestamp &&
                Double.compare(event.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, metric, timestamp, value);
    }

    /*
    private final String json;

    @JsonCreator
    public Event(@JsonProperty("json") String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return json.equals(event.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }
*/
}
