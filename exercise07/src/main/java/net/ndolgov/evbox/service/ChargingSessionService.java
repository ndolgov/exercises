package net.ndolgov.evbox.service;

import net.ndolgov.evbox.domain.ChargingSession;
import net.ndolgov.evbox.domain.Statistics;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manage charging sessions
 */
public interface ChargingSessionService {
    CompletableFuture<ChargingSession> create(String stationId);

    CompletableFuture<ChargingSession> finish(UUID sessionId);

    CompletableFuture<Iterator<ChargingSession>> retrieveAll();

    CompletableFuture<Statistics> lastMinStatistics();
}
