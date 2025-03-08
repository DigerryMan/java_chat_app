package org.homework.client;

import org.homework.server.ChatServer;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {

    private static Socket socket = null;
    private static DatagramSocket udpSocket = null;
    private static MulticastSocket multicastSocket = null;
    private static MessageSender messageSender = null;
    private static MessageReceiverThread messageReceiverThread = null;
    private static UdpMessageReceiverThread udpMessageReceiverThread = null;
    private static MulticastMessageReceiverThread multicastMessageReceiverThread = null;
    private static boolean connectionLost = false;
    public static final String MULTICAST_GROUP = "230.0.0.0";
    public static final int MULTICAST_PORT = 12346;
    private static final int THREAD_POOL_SIZE = 3;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args){
        try {
            socket = new Socket(ChatServer.HOST, ChatServer.PORT);
            udpSocket = new DatagramSocket(socket.getLocalPort());
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
            InetSocketAddress address = new InetSocketAddress(MULTICAST_GROUP, MULTICAST_PORT);
            multicastSocket.joinGroup(address, null);

            messageSender = new MessageSender(socket.getOutputStream(), udpSocket, multicastSocket, address);
            messageReceiverThread = new MessageReceiverThread(socket.getInputStream());
            udpMessageReceiverThread = new UdpMessageReceiverThread(udpSocket);
            multicastMessageReceiverThread = new MulticastMessageReceiverThread(multicastSocket);

            executor.execute(messageReceiverThread);
            executor.execute(udpMessageReceiverThread);
            executor.execute(multicastMessageReceiverThread);

            messageSender.sendMessagesFromConsole();
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + ChatServer.HOST);
        } catch (SocketException e) {
            System.out.println("Could not create socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not establish connection to server");
        }

        finally {
            try {
                disconnect();
            }catch (InterruptedException | IOException e){
                System.out.println("Could not disconnect from server");
            }

        }
    }

    private static void disconnect() throws InterruptedException, IOException {
        if (isConnectionEstablished()) {
            System.out.println("Closing connection");
            executor.shutdownNow();
            messageSender.end();
            socket.close();
            udpSocket.close();
            multicastSocket.close();
        }
    }

    private static boolean isConnectionEstablished(){
        return socket != null && messageReceiverThread != null && udpSocket != null
                && udpMessageReceiverThread != null && multicastSocket != null
                && multicastMessageReceiverThread != null;
    }

    public static void notifyDisconnect() {
        System.out.println("Connection to server lost!");
        connectionLost = true;
    }

    public static boolean isConnectionLost() {
        return connectionLost;
    }
}