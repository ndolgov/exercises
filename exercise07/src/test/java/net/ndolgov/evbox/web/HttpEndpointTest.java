package net.ndolgov.evbox.web;

import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.*;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import net.ndolgov.evbox.domain.ChargingSession;
import net.ndolgov.evbox.domain.Statistics;
import net.ndolgov.evbox.domain.StatusEnum;
import net.ndolgov.evbox.service.ChargingSessionService;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpEndpointTest extends JUnitRouteTest {
    @Test
    public void testEmptyInitially() {
        final ChargingSessionService service = mock(ChargingSessionService.class);
        when(service.retrieveAll()).thenReturn(CompletableFuture.completedFuture(Collections.emptyIterator()));

        testRoute(service)
            .run(HttpRequest.GET("/chargingSessions"))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntity("[[]]");
    }

    @Test
    public void testNewSessionCreation() {
        final String startedAt = "2019-10-20T12:47:12.859";
        final ChargingSession session = new ChargingSession(UUID.randomUUID(), "ABC-12345", startedAt, null, StatusEnum.IN_PROGRESS);
        final ChargingSessionService service = mock(ChargingSessionService.class);
        when(service.create(anyString())).thenReturn(CompletableFuture.completedFuture(session));

        testRoute(service)
            .run(HttpRequest.POST("/chargingSessions").withEntity(ContentTypes.APPLICATION_JSON, "{\"stationId\":\"ABC-12345\"}"))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(ChargingSession.class), session);
    }

    @Test
    public void testStoppingExistingSession() {
        final String startedAt = "2019-10-20T12:47:12.859";
        final String stoppedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        final ChargingSession session = new ChargingSession(UUID.randomUUID(), "ABC-12345", startedAt, stoppedAt, StatusEnum.FINISHED);
        final ChargingSessionService service = mock(ChargingSessionService.class);
        when(service.finish(any(UUID.class))).thenReturn(CompletableFuture.completedFuture(session));

        testRoute(service)
            .run(HttpRequest.PUT("/chargingSessions/" + session.getId()))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(ChargingSession.class), session);
    }

    @Test
    public void testSessionListing() {
        final String startedAt = "2019-10-20T12:47:12.859";
        final String stoppedAt = "2019-10-20T12:49:12.859";

        final ChargingSessionService service = mock(ChargingSessionService.class);
        final List<ChargingSession> sessions = new ArrayList<>();
        sessions.add(new ChargingSession(UUID.fromString("d9bb7458-d5d9-4de7-87f7-7f39edd51d15"), "ABC-12345", startedAt, stoppedAt, StatusEnum.IN_PROGRESS));
        sessions.add(new ChargingSession(UUID.fromString("d9bb7458-d5d9-4de7-87f7-7f39edd51d16"), "DEF-12346", startedAt, stoppedAt, StatusEnum.IN_PROGRESS));
        when(service.retrieveAll()).thenReturn(CompletableFuture.completedFuture(sessions.iterator()));

        testRoute(service)
            .run(HttpRequest.GET("/chargingSessions"))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntity("[[{\"id\":\"d9bb7458-d5d9-4de7-87f7-7f39edd51d15\",\"startedAt\":\"2019-10-20T12:47:12.859\",\"stationId\":\"ABC-12345\",\"status\":\"IN_PROGRESS\",\"stoppedAt\":\"2019-10-20T12:49:12.859\"},{\"id\":\"d9bb7458-d5d9-4de7-87f7-7f39edd51d16\",\"startedAt\":\"2019-10-20T12:47:12.859\",\"stationId\":\"DEF-12346\",\"status\":\"IN_PROGRESS\",\"stoppedAt\":\"2019-10-20T12:49:12.859\"}]]");
    }

    @Test
    public void testStatisticsRetrieval() {
        final Statistics stats = new Statistics(5, 3);
        final ChargingSessionService service = mock(ChargingSessionService.class);
        when(service.lastMinStatistics()).thenReturn(CompletableFuture.completedFuture(stats));

        testRoute(service)
            .run(HttpRequest.GET("/chargingSessions/summary"))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(Statistics.class), stats);
    }

    private TestRoute testRoute(ChargingSessionService service) {
        return testRoute(new HttpEndpoint(service, EntityStreamingSupport.json(), Jackson.marshaller()).routes());
    }
}