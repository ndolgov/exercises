package net.ndolgov.revo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AccountSummary {
    private final String accountId;

    private final int balance;

    @JsonCreator
    public AccountSummary(@JsonProperty("accountId") String accountId, @JsonProperty("balance") int balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountSummary accountSummary = (AccountSummary) o;
        return balance == accountSummary.balance &&
                accountId.equals(accountSummary.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, balance);
    }
}
