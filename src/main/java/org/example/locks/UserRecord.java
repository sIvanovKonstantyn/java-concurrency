package org.example.locks;

public class UserRecord {
    private volatile int balance;
    //other user fields...

    public void updateBalance(int amount) {
        synchronized(this) {
            if (balance + amount < 0) {
                return;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.balance += amount;
    }

    public int getBalance() {
        return balance;
    }
}
