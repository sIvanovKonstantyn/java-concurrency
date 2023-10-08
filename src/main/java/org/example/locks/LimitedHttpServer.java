package org.example.locks;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class LimitedHttpServer {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);
    private static final Map<String, Semaphore> limits = Map.of(
        "/users", new Semaphore(1),
        "/tasks", new Semaphore(99)
    );

    public static void main(String[] args) {
        new LimitedHttpServer().start();
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
                String endpoint = getEndpoint(clientSocket.getInputStream());

                Semaphore semaphore = limits.get(endpoint);

                if (semaphore == null) {
                    System.out.println("Unknown resource");
                    return;
                }
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                EXECUTOR_SERVICE.submit(new LimitedWorker(clientSocket, semaphore));

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getEndpoint(InputStream input) throws IOException {
        String request = new String(input.readNBytes(input.available()));
        String[] requestParts = request.split(" ");
        return requestParts[1];
    }
}