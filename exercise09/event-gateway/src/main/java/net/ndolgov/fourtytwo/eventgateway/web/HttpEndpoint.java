package net.ndolgov.fourtytwo.eventgateway.web;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import net.ndolgov.fourtytwo.eventgateway.domain.Event;
import net.ndolgov.fourtytwo.eventgateway.service.EtlService;

/**
 * HTTP endpoints
 */
public final class HttpEndpoint extends AllDirectives {
    private static final String EVENT = "event";

    private static final String STATS = "stats";

    private final EtlService service;

    public HttpEndpoint(EtlService service) {
        this.service = service;
    }

    public Route routes() {
        return concat(
            post(() ->
                path(EVENT, () ->
                    entity(Jackson.unmarshaller(Event.class), event ->
                        onSuccess(() ->
                            service.sendMessage(event),
                            unit -> complete(StatusCodes.OK)
                        )
                    )
                )
            ),

            get(() ->
                pathPrefix(STATS, () ->
                    pathEnd(() -> complete(StatusCodes.OK, "Forwarded events: " + service.eventCount()))
                )
            )
        );
    }
}
