package net.ndolgov.revo.service;

/**
 * A bank account that holds a balance in integer units such as cents
 */
final class Account {
    private int balance;

    public Account(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int withdraw(int amount) {
        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient funds: " + balance);
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Negative amount detected: " + amount);
        }

        balance -= amount;
        return balance;
    }

    public int deposit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Negative amount detected: " + amount);
        }
        balance += amount;
        return balance;
    }
}
