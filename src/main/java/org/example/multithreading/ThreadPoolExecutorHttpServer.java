package org.example.multithreading;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExecutorHttpServer {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);

    public static void main(String[] args) {
        new ThreadPoolExecutorHttpServer().start();
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
                EXECUTOR_SERVICE.submit(new SimpleWorker(clientSocket));

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}