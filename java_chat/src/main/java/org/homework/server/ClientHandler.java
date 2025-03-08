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
            System.out.println("[ClientHandler] Error reading from client " + id);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return id == that.id && Objects.equals(clientSocket, that.clientSocket) && Objects.equals(messageSender, that.messageSender) && Objects.equals(in, that.in);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientSocket, messageSender, id, in);
    }
}
