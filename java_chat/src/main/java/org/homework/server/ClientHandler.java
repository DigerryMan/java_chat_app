package org.homework.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final MessageSender messageSender;
    private final int id;
    private BufferedReader in;

    public ClientHandler(Socket socket, MessageSender messageSender, int id) {
        this.clientSocket = socket;
        this.messageSender = messageSender;
        this.id = id;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Client " + id + " connected.");
            messageSender.sendWelcomeMessage(this);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (Objects.equals(inputLine, "q")) {
                    break;
                }
                System.out.println("[Client_" + id + "] Broadcasted: " + inputLine);
                messageSender.broadcast("[USER_" + id + "]: " + inputLine, this);
            }
            messageSender.notifyDisconnect(this);
            System.out.println("Client " + id + " disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getId() {
        return id;
    }
    public Socket getClientSocket() {
        return clientSocket;
    }
}
