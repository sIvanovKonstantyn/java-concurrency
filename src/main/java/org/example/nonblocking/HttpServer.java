package org.example.nonblocking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static final UserBalanceInMemoryDB dbInstance = UserBalanceInMemoryDB.getInstance();
    private static final Thread monitoring = new Thread(() -> {
        while (true) {
            int balance = dbInstance.getUserBalance("user1");
            if (balance < 0) {
                System.out.println("USERS' balance less than 0!!!");
                System.exit(1);
            }
        }
    });

    public static void main(String[] args) {
        monitoring.start();
        new HttpServer().start();
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
                new Thread(new DBPersistenceWorker(clientSocket)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}