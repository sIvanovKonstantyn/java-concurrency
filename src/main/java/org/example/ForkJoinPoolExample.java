package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinPoolExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            data.add(i);
        }

        ForkJoinPool forkJoinPool = new ForkJoinPool(100);
        List<Integer> integers = forkJoinPool.submit(
            () -> data.parallelStream().map(i -> makeAJob(i)).toList()
        ).get();
    }

    private static int makeAJob(Integer i) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return i * 2;
    }
}
