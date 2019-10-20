package net.ndolgov.evbox.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StationId {
    private final String stationId;

    @JsonCreator
    public StationId(@JsonProperty("stationId") String stationId) {
        this.stationId = stationId;
    }

    public String getStationId() {
        return stationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationId stationId1 = (StationId) o;
        return stationId.equals(stationId1.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId);
    }
}
