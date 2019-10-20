package net.ndolgov.evbox.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class ChargingSession {
    private final UUID id;
    private final String stationId;
    private final String startedAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String stoppedAt;
    private final StatusEnum status;

    @JsonCreator
    public ChargingSession(@JsonProperty("id") UUID id,
                           @JsonProperty("stationId") String stationId,
                           @JsonProperty("startedAt") String startedAt,
                           @JsonProperty("stoppedAt") String stoppedAt,
                           @JsonProperty("status") StatusEnum status) {
        this.id = id;
        this.stationId = stationId;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getStoppedAt() {
        return stoppedAt;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public ChargingSession finish(String stoppedAt) {
        return new ChargingSession(id, stationId, startedAt, stoppedAt, StatusEnum.FINISHED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingSession that = (ChargingSession) o;
        return id.equals(that.id) &&
                stationId.equals(that.stationId) &&
                startedAt.equals(that.startedAt) &&
                Objects.equals(stoppedAt, that.stoppedAt) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stationId, startedAt, stoppedAt, status);
    }
}
