package org.example.multithreading;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Worker extends Thread {
    private Socket clientSocket;
    private volatile boolean hasJob = false;

    public Worker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public boolean hasJob() {
        return hasJob;
    }

    public void updateForNewJob(Socket clientSocket) {
        synchronized (this) {
            this.clientSocket = clientSocket;
            this.hasJob = true;
            notifyAll();
        }
    }

    @Override
    public void run() {
        while (true) {
            processJob();
            while (!hasJob) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void processJob() {
        hasJob = true;
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
        hasJob = false;
    }

    private void addHeader(StringBuilder sb, int contentLength) {
        sb.append("HTTP/1.1 200 OK\n")
            .append("Content-type: plain/text\n")
            .append("Content-length: ").append(contentLength)
            .append("\n")
            .append("\n");
    }
}
