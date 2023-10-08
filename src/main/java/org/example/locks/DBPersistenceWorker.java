package org.example.locks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DBPersistenceWorker implements Runnable {
    private final Socket clientSocket;
    private final UserBalanceInMemoryDB dbInstance = UserBalanceInMemoryDB.getInstance();

    public DBPersistenceWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        processJob();
    }

    private void processJob() {
        try (OutputStream output = clientSocket.getOutputStream();
             InputStream input = clientSocket.getInputStream()) {

            processRequest(input);
            processResponse(output);

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(InputStream input) throws IOException {
        String request = new String(input.readNBytes(input.available()));
        String[] requestParts = request.split("\r\n\r\n", 2);
        String[] requestBody = requestParts[1].split(";");
        dbInstance.updateUserBalance(requestBody[0], Integer.parseInt(requestBody[1]));
    }

    private void processResponse(OutputStream output) throws IOException {
        StringBuilder response = new StringBuilder();
        String responseBody = "Saved by thread [" + Thread.currentThread().getId() + "]";
        addHeader(response, responseBody.length());
        response.append(responseBody);
        output.write(response.toString().getBytes());
        output.flush();
    }

    private void addHeader(StringBuilder sb, int contentLength) {
        sb.append("HTTP/1.1 200 OK\n")
            .append("Content-type: plain/text\n")
            .append("Content-length: ").append(contentLength)
            .append("\n")
            .append("\n");
    }
}
