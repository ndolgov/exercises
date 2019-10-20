package net.ndolgov.evbox.web;

import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Source;
import net.ndolgov.evbox.domain.StationId;
import net.ndolgov.evbox.service.ChargingSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static akka.http.javadsl.server.PathMatchers.segment;

/**
 * HTTP endpoints
 */
public final class HttpEndpoint extends AllDirectives {
    private final Logger log = LoggerFactory.getLogger(HttpEndpoint.class);

    private static final String CHARGING_SESSIONS = "chargingSessions";

    private final ChargingSessionService service;

    private final EntityStreamingSupport support;

    private final Marshaller<?, RequestEntity> marshaller;

    public HttpEndpoint(ChargingSessionService service, EntityStreamingSupport support, Marshaller<?, RequestEntity> marshaller) {
        this.service = service;
        this.support = support;
        this.marshaller = marshaller;
    }

    public Route routes() {
        return concat(
            get(() ->
                pathPrefix(CHARGING_SESSIONS, () -> concat(
                    pathEnd(() -> completeOKWithSource(
                        Source.fromCompletionStage(service.retrieveAll()),
                        marshaller(),
                        support)
                    )
                        ,
                    path("summary", () ->
                        onSuccess(() ->
                            service.lastMinStatistics(),
                            stats -> complete(StatusCodes.OK, stats, marshaller())
                        )
                    ))
                )
            ),

            post(() ->
                path(CHARGING_SESSIONS, () ->
                    entity(Jackson.unmarshaller(StationId.class), body ->
                        onSuccess(() ->
                            service.create(body.getStationId()),
                            session -> complete(StatusCodes.OK, session, marshaller())
                        )
                    )
                )
            ),

            put(() ->
                pathPrefix(CHARGING_SESSIONS, () ->
                    path(segment(), (String sessionId) ->
                        onSuccess(() ->
                            service.finish(UUID.fromString(sessionId)),
                            session -> complete(StatusCodes.OK, session, marshaller())
                        )
                    )
                )
            )
        );
    }

    private <T> Marshaller<T, RequestEntity> marshaller() {
        return (Marshaller<T, RequestEntity>) marshaller;
    }
}
