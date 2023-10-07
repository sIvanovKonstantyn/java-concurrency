package org.example.multithreading;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class MultithreadThreadHttpServer {

    private static final int THREADS_LIMIT = 100;
    private static final Worker[] THREAD_POOL = new Worker[THREADS_LIMIT];
    private static final Random RANDOM = new Random();

    private int capacity;

    public static void main(String[] args) {
        new MultithreadThreadHttpServer().start();
    }

    private void start() {
        int port = 8080;

        try {
            // Create a server socket on port 8080
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            boolean first = true;

            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                if (first) {
                    clientSocket.close();
                    first = false;
                    continue;
                } else {
                    first = true;
                }


                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                if (capacity < THREADS_LIMIT) {
                    // Handle the client request
                    Worker worker = new Worker(clientSocket);
                    THREAD_POOL[capacity++] = worker;
                    worker.start();
                } else {
                    Worker worker = findFirstFreeOrRandom();

                    while (worker.hasJob()) {
                        synchronized (worker) {
                            worker.wait();
                        }
                    }
                    worker.updateForNewJob(clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Worker findFirstFreeOrRandom() {
        for (Worker worker : THREAD_POOL) {
            if (!worker.hasJob()) {
                return worker;
            }
        }

        return THREAD_POOL[RANDOM.nextInt(THREADS_LIMIT)];
    }
}