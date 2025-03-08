package org.homework.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ChatServer {

    public static final String HOST = "localhost";
    public static final int PORT = 12345;
    private static int clientId = 0;
    private static final int MAX_CLIENTS = 2;

    public static void main(String[] args){

        System.out.println("Chat Server started!");
        ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTS);
        MessageSender messageSender = new MessageSender();
        UDPHandler udpHandler = null;

        try (ServerSocket tcpSocket = new ServerSocket(PORT); DatagramSocket udpSocket = new DatagramSocket(PORT)) {
            udpHandler = new UDPHandler(udpSocket, messageSender);
            udpHandler.start();
            while (true) {
                if(messageSender.getNumberOfClients() < MAX_CLIENTS) {
                    acceptNewClient(tcpSocket, messageSender, executor);
                }
            }
        } catch (IOException e) {;
            System.out.println("[ERROR] Could not listen on port " + PORT);
        } finally {
            if(udpHandler != null)  udpHandler.end();
            executor.shutdown();
        }
    }

    private static void acceptNewClient(ServerSocket serverSocket, MessageSender messageSender, ExecutorService executor)  throws IOException {
        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket, messageSender, clientId++);
        messageSender.add(clientHandler, clientSocket.getOutputStream());
        executor.execute(clientHandler);
    }
}