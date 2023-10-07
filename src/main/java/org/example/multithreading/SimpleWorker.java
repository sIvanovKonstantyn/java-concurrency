package org.example.multithreading;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SimpleWorker implements Runnable {
    private final Socket clientSocket;
    public SimpleWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        processJob();
    }

    private void processJob() {
        try(OutputStream output = clientSocket.getOutputStream()) {
            StringBuilder response = new StringBuilder();
            String responseBody = "Hello from thread [" + Thread.currentThread().getId() + "]";
            addHeader(response, responseBody.length());
            response.append(responseBody);
            output.write(response.toString().getBytes());
            output.flush();

            clientSocket.close();
            Thread.sleep(10000);
            synchronized (this) {
                notifyAll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHeader(StringBuilder sb, int contentLength) {
        sb.append("HTTP/1.1 200 OK\n")
            .append("Content-type: plain/text\n")
            .append("Content-length: ").append(contentLength)
            .append("\n")
            .append("\n");
    }
}
