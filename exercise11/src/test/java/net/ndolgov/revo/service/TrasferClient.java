package net.ndolgov.revo.service;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * Make a given number of small transfers between two random accounts
 */
final class TrasferClient implements Runnable {
    private final AccountService service;
    private final int repetitions;
    private final CyclicBarrier barrier;
    private final String[] fromAccounts;
    private final String[] toAccounts;

    public TrasferClient(AccountService service, int repetitions, String[] accountIds, CyclicBarrier barrier) {
        this.service = service;
        this.repetitions = repetitions;
        this.barrier = barrier;

        final Random random = new Random();
        random.setSeed(this.hashCode());

        final int nAccounts = accountIds.length;

        fromAccounts = new String[repetitions];
        toAccounts = new String[repetitions];
        for (int i = 0; i < repetitions; i++) {
            final int from = random.nextInt(nAccounts);
            int to = from;
            while (to == from) {
                to = random.nextInt(nAccounts);
            }

            fromAccounts[i] = accountIds[from];
            toAccounts[i] = accountIds[to];
        }
    }

    @Override
    public void run() {
        try {
            barrier.await(10, TimeUnit.SECONDS);

            for (int i = 0; i < repetitions; i++) {
                service.transfer(fromAccounts[i], toAccounts[i], 3);
            }

            barrier.await(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            AccountServiceImplTest.exception = e;
        }
    }
}
