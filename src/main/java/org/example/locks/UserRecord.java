package org.example.locks;

import java.util.concurrent.locks.ReentrantLock;

public class UserRecord {
    private ReentrantLock lock = new ReentrantLock();
    private volatile int balance;
    //other user fields...

    public void updateBalance(int amount) {
        try {
            lock.lock();
            if (balance + amount < 0) {
                return;
            }

            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        this.balance += amount;
    }

    public int getBalance() {
        return balance;
    }
}
