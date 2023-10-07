package org.example.multithreading;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OneThreadHttpServer {
    public static void main(String[] args) {
        int port = 8080;

        try {
            // Create a server socket on port 8080
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                // Handle the client request
                handleClientRequest(clientSocket);

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try(OutputStream output = clientSocket.getOutputStream()) {
            StringBuilder response = new StringBuilder();
            String responseBody = "Hello, World!";
            addHeader(response, responseBody.length());
            response.append(responseBody);
            Thread.sleep(5000);
            output.write(response.toString().getBytes());
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addHeader(StringBuilder sb, int contentLength) {
        sb.append("HTTP/1.1 200 OK\n")
            .append("Content-type: plain/text\n")
            .append("Content-length: ").append(contentLength)
            .append("\n")
            .append("\n");
    }
}