package net.ndolgov.revo.service;

import net.ndolgov.revo.domain.AccountSummary;

import java.util.concurrent.CompletableFuture;

/**
 * Manage account balance
 */
public interface AccountService {
    AccountSummary createAccount();

    AccountSummary getBalance(String accountId);

    AccountSummary deposit(String accountId, int amount);

    AccountSummary transfer(String fromAccountId, String toAccountId, int amount);
}
