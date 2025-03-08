package org.homework.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageSender {

    List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    Map<ClientHandler, PrintWriter> clientOuts = new ConcurrentHashMap<>();

    void add(ClientHandler client, OutputStream outputStream) {
        clients.add(client);
        clientOuts.put(client, new PrintWriter(outputStream, true));
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                sendMessage(client, message);
            }
        }
    }

    public void notifyDisconnect(ClientHandler sender) {
        clients.remove(sender);
        for (ClientHandler client : clients) {
            if (client != sender) {
                sendMessage(client, "User " + sender.getId() + " left the chat.");
            }
        }
    }

    public void sendWelcomeMessage(ClientHandler client) {
        sendMessage(client, "Welcome to the chat, CLIENT_" + client.getId() + "!");
        sendMessage(client, "Type Your Message: ");
    }

    private void sendMessage(ClientHandler client, String message) {
        clientOuts.get(client).println(message);
    }

    public void udpBroadcast(DatagramSocket udpSocket, String message, DatagramPacket receivePacket) throws IOException {
        for (ClientHandler client : clients) {
            Socket clientSocket = client.getClientSocket();
            if (client.getClientSocket().getPort() != receivePacket.getPort()) {
                DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(),
                        clientSocket.getInetAddress(), clientSocket.getPort());
                udpSocket.send(sendPacket);
            }
        }
    }

    public int getNumberOfClients() {
        return clients.size();
    }
}
