package org.example.locks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class LimitedWorker implements Runnable {
    private final Socket clientSocket;
    private final Semaphore semaphore;
    public LimitedWorker(Socket clientSocket, Semaphore semaphore) {
        this.clientSocket = clientSocket;
        this.semaphore = semaphore;
    }


    @Override
    public void run() {
        processJob();
    }

    private void processJob() {
        try(OutputStream output = clientSocket.getOutputStream()) {
            StringBuilder response = new StringBuilder();
            String responseBody = "Hello from thread [" + Thread.currentThread().threadId() + "]";
            addHeader(response, responseBody.length());
            response.append(responseBody);
            output.write(response.toString().getBytes());
            output.flush();

            clientSocket.close();
            Thread.sleep(10000);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
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
