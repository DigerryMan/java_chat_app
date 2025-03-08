package org.homework.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageSender {

    Map<ClientHandler, PrintWriter> clientMap = new ConcurrentHashMap<>();

    void add(ClientHandler client, OutputStream outputStream) {
        clientMap.put(client, new PrintWriter(outputStream, true));
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientMap.keySet()) {
            if (client != sender) {
                sendMessage(client, message);
            }
        }
    }

    public void disconnect(ClientHandler client) {
        clientMap.remove(client).close();
        for (ClientHandler other : clientMap.keySet()) {
            if (other != client) {
                sendMessage(other, "User " + client.getId() + " left the chat.");
            }
        }
    }

    public void sendWelcomeMessage(ClientHandler client) {
        sendMessage(client, "Welcome to the chat, CLIENT_" + client.getId() + "!");
        sendMessage(client, "Type Your Message: ");
    }

    private void sendMessage(ClientHandler client, String message) {
        clientMap.get(client).println(message);
    }

    public void udpBroadcast(DatagramSocket udpSocket, String message, DatagramPacket receivePacket) throws IOException {
        for (ClientHandler client : clientMap.keySet()) {
            Socket clientSocket = client.getClientSocket();
            if (client.getClientSocket().getPort() != receivePacket.getPort()) {
                DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(),
                        clientSocket.getInetAddress(), clientSocket.getPort());
                udpSocket.send(sendPacket);
            }
        }
    }

    public int getNumberOfClients() {
        return clientMap.keySet().size();
    }
}
