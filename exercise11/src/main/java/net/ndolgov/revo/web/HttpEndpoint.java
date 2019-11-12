package net.ndolgov.revo.web;

import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import net.ndolgov.revo.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static akka.http.javadsl.server.PathMatchers.integerSegment;
import static akka.http.javadsl.server.PathMatchers.segment;

/**
 * HTTP endpoints
 */
public final class HttpEndpoint extends AllDirectives {
    private final Logger log = LoggerFactory.getLogger(HttpEndpoint.class);

    private static final String ACCOUNT = "account";

    private static final String TRANSFER = "transfer";

    private final AccountService service;

    private final Marshaller<?, RequestEntity> marshaller;

    public HttpEndpoint(AccountService service, Marshaller<?, RequestEntity> marshaller) {
        this.service = service;
        this.marshaller = marshaller;
    }

    public Route routes() {
        return concat(
            get(() ->
                pathPrefix(ACCOUNT, () ->
                    path(segment(), (String accountId) ->
                        complete(StatusCodes.OK, service.getBalance(accountId), marshaller())
                    )
                )
            ),

            post(() -> concat(
                path(ACCOUNT, () ->
                    complete(StatusCodes.OK, service.createAccount(), marshaller())
                ),
                pathPrefix(TRANSFER, () ->
                    pathPrefix(segment(), (String fromAccountId) ->
                        pathPrefix(segment(), (String toAccountId) ->
                            path(integerSegment(), (Integer amount) ->
                                complete(StatusCodes.OK, service.transfer(fromAccountId, toAccountId, amount), marshaller())
                            )
                        )
                    )
                )

            )),

            put(() ->
                pathPrefix(ACCOUNT, () ->
                    pathPrefix(segment(), (String accountId) ->
                        path(integerSegment(), (Integer amount) ->
                            complete(StatusCodes.OK, service.deposit(accountId, amount), marshaller())
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
