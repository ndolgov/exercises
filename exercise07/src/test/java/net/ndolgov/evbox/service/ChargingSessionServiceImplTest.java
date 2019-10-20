package net.ndolgov.evbox.service;

import net.ndolgov.evbox.domain.ChargingSession;
import net.ndolgov.evbox.domain.Statistics;
import net.ndolgov.evbox.domain.StatusEnum;
import net.ndolgov.evbox.service.Windows.WindowStats;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static net.ndolgov.evbox.service.Windows.tumblingWindow;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChargingSessionServiceImplTest {
    private static final String STATION_ID = "ABC-12345";
    
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2019-10-20T12:15:00.00Z"), ZoneOffset.UTC);

    @Test
    public void testSessionLifecycle() throws Exception{
        final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        when(executor.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenReturn(null);
        final WindowStats<Statistics> stats = tumblingWindow(new Statistics(0, 0), 60, executor);

        final ChargingSessionService service = new ChargingSessionServiceImpl(stats, CLOCK);
        final Statistics initialStats = service.lastMinStatistics().get();
        assertEquals(new Statistics(0,0), initialStats);

        final ChargingSession session1 = service.create(STATION_ID).get();
        final ChargingSession session2 = service.create(STATION_ID).get();
        final ChargingSession session3 = service.create(STATION_ID).get();
        assertEquals(new Statistics(3,0), service.lastMinStatistics().get());

        final Map<UUID, ChargingSession> snapshotA = snapshot(service);
        assertEquals(3, snapshotA.size());
        assertStarted(snapshotA.get(session1.getId()));
        assertStarted(snapshotA.get(session2.getId()));
        assertStarted(snapshotA.get(session3.getId()));

        service.finish(session2.getId());
        assertEquals(new Statistics(3,1), service.lastMinStatistics().get());
        final Map<UUID, ChargingSession> snapshotB = snapshot(service);
        assertEquals(3, snapshotB.size());
        assertStarted(snapshotB.get(session1.getId()));
        assertFinished(snapshotB.get(session2.getId()));
        assertStarted(snapshotB.get(session3.getId()));

        service.finish(session1.getId());
        service.finish(session3.getId());
        assertEquals(new Statistics(3,3), service.lastMinStatistics().get());
        final Map<UUID, ChargingSession> snapshotC = snapshot(service);
        assertEquals(3, snapshotC.size());
        assertFinished(snapshotC.get(session1.getId()));
        assertFinished(snapshotC.get(session2.getId()));
        assertFinished(snapshotC.get(session3.getId()));
    }

    private void assertFinished(ChargingSession session) {
        assertEquals(StatusEnum.FINISHED, session.getStatus());
        assertNotNull(session.getStoppedAt());
    }

    private void assertStarted(ChargingSession session) {
        assertEquals(StatusEnum.IN_PROGRESS, session.getStatus());
        assertNull(session.getStoppedAt());
    }

    private Map<UUID, ChargingSession> snapshot(ChargingSessionService service) throws Exception {
        final Map<UUID, ChargingSession> map = new HashMap<>();
        service.retrieveAll().get().forEachRemaining(session -> map.put(session.getId(), session));
        return map;
    }
}