package net.ndolgov.revo.service;

import net.ndolgov.revo.domain.AccountSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountServiceImpl implements AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final ConcurrentMap<String, Account> accounts;

    public AccountServiceImpl() {
        this.accounts = new ConcurrentHashMap<>(1024);
    }

    @Override
    public AccountSummary createAccount() {
        final String accountId = UUID.randomUUID().toString();
        final Account account = new Account(0);

        final Account existing = accounts.putIfAbsent(accountId, account);
        if (existing != null) {
            throw new IllegalArgumentException("Account already exists: " + accountId);
        }

        log.info("Created account " + accountId);

        return new AccountSummary(accountId, 0);
    }

    @Override
    public AccountSummary getBalance(String accountId) {
        final Account account = findAccount(accountId);

        final int balance;
        synchronized (account) {
            balance = account.getBalance();
        }

        return new AccountSummary(accountId, balance);
    }

    @Override
    public AccountSummary deposit(String accountId, int amount) {
        final Account account = findAccount(accountId);

        final int balance;
        synchronized (account) {
            balance = account.deposit(amount);
        }

        return new AccountSummary(accountId, balance);
    }

    @Override
    public AccountSummary transfer(String fromAccountId, String toAccountId, int amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Transferring to the same account detected: " + fromAccountId);
        }

        final Account fromAccount = findAccount(fromAccountId);
        final Account toAccount = findAccount(toAccountId);

        // deadlock-safe locking protocol: lock first on the account with a smaller accountId
        final int cmp = fromAccountId.compareTo(toAccountId);

        final boolean inRequestedOrded = cmp < 0;
        final Account lockFirst = inRequestedOrded ? fromAccount : toAccount;
        final Account lockSecond = inRequestedOrded ? toAccount: fromAccount;

        final int balance;
        synchronized (lockFirst) {
            synchronized (lockSecond) {
                final int fromAccountBalance = fromAccount.getBalance();
                if (fromAccountBalance < amount) {
                    throw new IllegalArgumentException("Insufficient funds: " + fromAccountBalance);
                }

                balance = fromAccount.withdraw(amount);
                toAccount.deposit(amount);
            }
        }

        return new AccountSummary(fromAccountId, balance);
    }

    private Account findAccount(String accountId) {
        final Account account = accounts.get(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        return account;
    }
}
