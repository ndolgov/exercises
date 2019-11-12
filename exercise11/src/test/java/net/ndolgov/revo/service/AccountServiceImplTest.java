package net.ndolgov.revo.service;

import net.ndolgov.revo.domain.AccountSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AccountServiceImplTest {
    /** The last exception thrown from a test thread, to be checked after each concurrent test */
    static volatile Exception exception;

    @Before
    public void beforeTest() {
        exception = null;
    }

    @After
    public void afterTest() {
        if (exception != null) {
            exception.printStackTrace();
            fail("Exception caught in a child thread: " + exception.getMessage());
        }
    }

    @Test
    public void testConcurrentTransfersPreserveTotalBalance() throws Exception {
        final AccountService service = new AccountServiceImpl();
        final int nClients = 16;
        final int nAccounts = 10;
        final String[] accountIds = new String[nAccounts];

        final Random random = new Random();
        random.setSeed(this.hashCode());
        final int[] initialBalances = new int[nAccounts];
        int sum = 0;
        for (int i = 0; i < nAccounts; i++) {
            accountIds[i] = service.createAccount().getAccountId();
            initialBalances[i] = random.nextInt(10_000);
            service.deposit(accountIds[i], initialBalances[i]);
            sum += initialBalances[i];
        }

        final TrasferClient[] clients = new TrasferClient[nClients];
        final int repetitions = 1000;
        final CyclicBarrier barrier = new CyclicBarrier(nClients + 1);

        final ExecutorService executor = Executors.newFixedThreadPool(nClients + 1);
        try {
            for (int i = 0; i < nClients; i++) {
                clients[i] = new TrasferClient(service, repetitions, accountIds, barrier);
                executor.execute(clients[i]);
            }

            barrier.await(10, TimeUnit.SECONDS); // start

            barrier.await(1, TimeUnit.MINUTES); // wait for completion

        } finally {
            executor.shutdownNow();
        }

        final int expectedTotalBalance = Arrays.stream(initialBalances).sum();
        final int actualTotalBalance = Arrays.stream(accountIds)
            .map(service::getBalance)
            .map(AccountSummary::getBalance)
            .mapToInt(Integer::intValue)
            .sum();

        assertEquals(expectedTotalBalance, sum);
        assertEquals(expectedTotalBalance, actualTotalBalance);
    }
}