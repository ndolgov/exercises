package net.ndolgov.revo.web;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.*;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import net.ndolgov.revo.domain.AccountSummary;
import net.ndolgov.revo.service.AccountService;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpEndpointTest extends JUnitRouteTest {
    @Test
    public void testAccountCreation() {
        final AccountService service = mock(AccountService.class);
        final AccountSummary summary = new AccountSummary(UUID.randomUUID().toString(), 0);
        when(service.createAccount()).thenReturn(summary);

        testRoute(service)
            .run(HttpRequest.POST("/account"))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(AccountSummary.class), summary);
    }

    @Test
    public void testDeposit() {
        final AccountService service = mock(AccountService.class);
        final String accountId = UUID.randomUUID().toString();
        final AccountSummary summary = new AccountSummary(accountId, 31415);
        when(service.deposit(anyString(), anyInt())).thenReturn(summary);

        testRoute(service)
            .run(HttpRequest.PUT("/account/" + accountId + "/300"))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(AccountSummary.class), summary);
    }

    @Test
    public void testBalanceCheck() {
        final AccountService service = mock(AccountService.class);
        final String accountId = UUID.randomUUID().toString();
        final AccountSummary summary = new AccountSummary(accountId, 281);
        when(service.getBalance(anyString())).thenReturn(summary);

        testRoute(service)
            .run(HttpRequest.GET("/account/" + accountId))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(AccountSummary.class), summary);
    }

    @Test
    public void testTransfer() {
        final String fromAccountId = UUID.randomUUID().toString();
        final String toAccountId = UUID.randomUUID().toString();
        final AccountSummary summary = new AccountSummary(fromAccountId, 42);

        final AccountService service = mock(AccountService.class);
        when(service.transfer(anyString(), anyString(), anyInt())).thenReturn(summary);

        testRoute(service)
            .run(HttpRequest.POST("/transfer/" + fromAccountId + "/" + toAccountId + "/" + 33))
            .assertStatusCode(StatusCodes.OK)
            .assertMediaType("application/json")
            .assertEntityAs(Jackson.unmarshaller(AccountSummary.class), summary);
    }

    private TestRoute testRoute(AccountService service) {
        return testRoute(new HttpEndpoint(service, Jackson.marshaller()).routes());
    }
}