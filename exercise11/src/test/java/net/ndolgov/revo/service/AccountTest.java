package net.ndolgov.revo.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountTest {
    @Test
    public void testBalanceChanges() {
        final Account account = new Account(0);
        assertEquals(0, account.getBalance());

        assertEquals(50, account.deposit(50));
        assertEquals(50, account.getBalance());

        assertEquals(30, account.withdraw(20));
        assertEquals(30, account.getBalance());
    }
}