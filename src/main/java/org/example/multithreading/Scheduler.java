package org.example.multithreading;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static final ScheduledExecutorService SCHEDULER =
        Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {

        Map<Method, Object> scheduledMethods = findScheduledMethods();

        scheduledMethods.forEach(
            (m,o) -> SCHEDULER.scheduleAtFixedRate(
                () -> {
                    try {
                        m.invoke(o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, 0L, 10, TimeUnit.SECONDS
            )
        );


        while (true) {

        }
    }

    private static Map<Method, Object> findScheduledMethods() {
        //Here can be a package scan for all the needed classes. We will leave just one class
        // for simplicity

        Method[] methods = ScheduledWorker.class.getMethods();
        Map<Method, Object> result = new HashMap<>();
        for (Method method : methods) {
            if (method.getAnnotation(Scheduled.class) != null) {
                try {
                    result.put(
                        method,
                        ScheduledWorker.class.getDeclaredConstructor().newInstance()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return result;
    }
}

class ScheduledWorker {

    @Scheduled
    public void execute() {
        System.out.println("Hello from scheduler: [" + Thread.currentThread().getId() + "]");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

@Retention(RetentionPolicy.RUNTIME)
@interface Scheduled {

}
