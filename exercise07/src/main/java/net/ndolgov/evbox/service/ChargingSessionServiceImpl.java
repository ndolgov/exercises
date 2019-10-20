package net.ndolgov.evbox.service;

import net.ndolgov.evbox.domain.ChargingSession;
import net.ndolgov.evbox.domain.Statistics;
import net.ndolgov.evbox.domain.StatusEnum;
import net.ndolgov.evbox.service.Windows.WindowStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public final class ChargingSessionServiceImpl implements ChargingSessionService {
    private static final String NOT_YET = null;

    private final Logger log = LoggerFactory.getLogger(ChargingSessionService.class);

    private final Clock clock;

    private final ConcurrentMap<UUID, ChargingSession> sessions;

    private final WindowStats<Statistics> stats;

    public ChargingSessionServiceImpl(WindowStats<Statistics> stats, Clock clock) {
        this.clock = clock;
        this.sessions = new ConcurrentHashMap<>(1024);
        this.stats = stats;
    }

    @Override
    public CompletableFuture<ChargingSession> create(String stationId) {
        log.info("Starting a new session at station " + stationId);

        final UUID sessionId = UUID.randomUUID();

        final ChargingSession session = new ChargingSession(
            sessionId,
            stationId,
            now(),
            NOT_YET,
            StatusEnum.IN_PROGRESS);

        final ChargingSession existing = sessions.putIfAbsent(sessionId, session);
        if (existing != null) {
            return failed(new IllegalArgumentException("Session with this id already exists: " + sessionId));
        }

        stats.onChange(Statistics::incStarted);

        log.info("Created session " + sessionId);

        return CompletableFuture.completedFuture(session);
    }

    @Override
    public CompletableFuture<ChargingSession> finish(UUID sessionId) {
        log.info("Finishing session " + sessionId);

        final ChargingSession started = sessions.get(sessionId);
        if (started == null) {
            return failed(new IllegalArgumentException("No session to finish found by id: " + sessionId));
        }
        if (started.getStatus() == StatusEnum.FINISHED) {
            return failed(new IllegalArgumentException("Session already finished: " + sessionId));
        }

        final ChargingSession finished = started.finish(now());

        if (!sessions.replace(sessionId, started, finished)) {
            return failed(new IllegalArgumentException("Session was modified by another user: " + sessionId));
        }

        stats.onChange(Statistics::incFinished);

        return CompletableFuture.completedFuture(finished);
    }

    @Override
    public CompletableFuture<Iterator<ChargingSession>> retrieveAll() {
        log.info("Listing the sessions being in progress");
        return CompletableFuture.completedFuture(sessions.values().iterator());
    }

    @Override
    public CompletableFuture<Statistics> lastMinStatistics() {
        log.info("Collecting statistics");
        return CompletableFuture.completedFuture(stats.stats());
    }

    private String now() {
        return LocalDateTime.now(clock).format(ISO_DATE_TIME);
    }

    private static <T> CompletableFuture<T> failed(Throwable error) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(error);
        return future;
    }
}
