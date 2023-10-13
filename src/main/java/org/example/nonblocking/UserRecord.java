package org.example.nonblocking;

import java.util.concurrent.atomic.AtomicInteger;

public class UserRecord {
    private final AtomicInteger balance = new AtomicInteger();
    //other user fields...

    public void updateBalance(int amount) {

        try {
            int expectedValue = balance.get();
            if (expectedValue + amount < 0) {
                return;
            }

            Thread.sleep(10000);

            while (!this.balance.compareAndSet(expectedValue, expectedValue + amount)) {
                expectedValue = this.balance.get();
                if (expectedValue + amount < 0) {
                    System.out.println("Balance update was ignored");
                    return;
                }
            }
            System.out.println("Actual balance: " + balance.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getBalance() {
        return balance.get();
    }
}
