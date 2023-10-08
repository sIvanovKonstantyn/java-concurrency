package org.example.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PaymentsProcessor {
    public static void main(String[] args) {
        List<Payment> payments = List.of(
            new Payment("user1", "paymentType1", 100),
            new Payment("user2", "paymentType1", 200),
            new Payment("user3", "paymentType1", 200),
            new Payment("user4", "paymentType1", 200)
        );

        SplitTask task = new SplitTask(payments, new GroupTask());
        task.process();
    }
}

enum PaymentStatus {
    VERIFIED, ERROR, NEW
}

class Payment {
    private String userName;
    private String paymentType;

    private int amount;

    private PaymentStatus status = PaymentStatus.NEW;

    public Payment(String userName, String paymentType, int amount) {
        this.userName = userName;
        this.paymentType = paymentType;
        this.amount = amount;
    }

    public void verified() {
        this.status = PaymentStatus.VERIFIED;
    }
    public void error() {
        this.status = PaymentStatus.ERROR;
    }

}

class SplitTask {
    private final List<Payment> payments;

    private final GroupTask groupTask;

    SplitTask(List<Payment> payments, GroupTask groupTask) {
        this.payments = payments;
        this.groupTask = groupTask;
    }
    public void process() {
        CountDownLatch latch = new CountDownLatch(payments.size());
        payments.forEach(
            p ->
                new Thread(
                    () -> new VerificationTask(latch, groupTask, p)
                        .process()
                ).start()
        );
    }

}
class VerificationTask {

    private final CountDownLatch latch;
    private final GroupTask nextTask;

    private final Payment payment;

    VerificationTask(CountDownLatch latch, GroupTask nextTask, Payment payment) {
        this.latch = latch;
        this.nextTask = nextTask;
        this.payment = payment;
    }
    public void process() {
        try {
            System.out.println(
                "Payment verification: " + payment + ", thread: " + Thread.currentThread().getId());
            payment.verified();
            nextTask.addPayment(payment);
            latch.countDown();
            latch.await();
            nextTask.process();
        } catch (Exception e) {
            payment.error();
        }
    }

}
class GroupTask {

    private final List<Payment> payments = new ArrayList<>();

    public void process() {
        System.out.println("Number of payments: " + payments.size());
    }
    public void addPayment(Payment payment) {
        payments.add(payment);
    }
}
