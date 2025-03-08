package org.homework.client;

import org.homework.server.ChatServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


import static java.lang.Thread.sleep;

public class ChatClient {

    private static Socket socket = null;
    private static DatagramSocket udpSocket = null;
    private static MessageReceiverThread messageReceiverThread = null;
    private static UdpMessageReceiverThread udpMessageReceiverThread = null;
    private static MessageSender messageSender = null;
    private static boolean connectionLost = false;

    public static void main(String[] args){
        try {
            socket = new Socket(ChatServer.HOST, ChatServer.PORT);
            udpSocket = new DatagramSocket(socket.getLocalPort());
            messageSender = new MessageSender(socket.getOutputStream(), udpSocket);
            messageReceiverThread = new MessageReceiverThread(socket.getInputStream());
            udpMessageReceiverThread = new UdpMessageReceiverThread(udpSocket);

            messageReceiverThread.start();
            udpMessageReceiverThread.start();
            messageSender.sendMessagesFromConsole();
        }catch (UnknownHostException e) {
            System.out.println("Unknown host: " + ChatServer.HOST);
        }
        catch (SocketException e) {
            System.out.println("Could not create socket");
        } catch (IOException e) {
            System.out.println("Could not establish connection to server.");
        }

        finally {
            try {
                disconnect();
            }catch (InterruptedException | IOException e){
                System.out.println("Could not disconnect from server.");
            }

        }
    }

    private static void disconnect() throws InterruptedException, IOException {
        if (isConnectionEstablished()) {
            System.out.println("Closing connection");
            messageReceiverThread.end();
            udpMessageReceiverThread.end();
            sleep(5);
            if(messageReceiverThread.isAlive()) messageReceiverThread.interrupt();
            if(udpMessageReceiverThread.isAlive()) udpMessageReceiverThread.interrupt();
            messageSender.end();
            socket.close();
            udpSocket.close();
        }
    }

    private static boolean isConnectionEstablished(){
        return socket != null && messageReceiverThread != null && udpSocket != null && udpMessageReceiverThread != null;
    }

    public static void notifyDisconnect() {
        System.out.println("Connection to server lost!");
        connectionLost = true;
    }

    public static boolean isConnectionLost() {
        return connectionLost;
    }
}