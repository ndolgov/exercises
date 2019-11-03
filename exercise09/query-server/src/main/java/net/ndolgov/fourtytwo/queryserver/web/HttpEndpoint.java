package net.ndolgov.fourtytwo.queryserver.web;

import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.javadsl.Source;
import net.ndolgov.fourtytwo.queryserver.domain.RequestSqlQuery;
import net.ndolgov.fourtytwo.queryserver.service.QueryService;

/**
 * HTTP endpoints
 */
public final class HttpEndpoint extends AllDirectives {
    private static final String EXECUTE_QUERY = "executeQuery";

    private final QueryService service;

    private final EntityStreamingSupport support;

    private final Marshaller<?, RequestEntity> marshaller;

    public HttpEndpoint(QueryService service, EntityStreamingSupport support, Marshaller<?, RequestEntity> marshaller) {
        this.service = service;
        this.support = support;
        this.marshaller = marshaller;
    }

    public Route routes() {
        return concat(
            get(() ->
                pathPrefix(EXECUTE_QUERY, () -> concat(
//                        entity(Jackson.unmarshaller(RequestSqlQuery.class), requestQuery ->
//                            onSuccess(() ->
//                                service.executeQuery(requestQuery.getSqlQuery()),
//                                rows -> complete(StatusCodes.OK, rows, marshaller())
//                            )
//                        )

                    entity(Jackson.unmarshaller(RequestSqlQuery.class), requestQuery -> completeOKWithSource(
                        Source.fromCompletionStage(service.executeQuery(requestQuery.getSqlQuery())), marshaller(), support)
                    )
                )
            ))
        );
    }

    private <T> Marshaller<T, RequestEntity> marshaller() {
        return (Marshaller<T, RequestEntity>) marshaller;
    }
}
