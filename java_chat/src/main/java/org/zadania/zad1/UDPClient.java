package org.zadania.zad1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public static void main(String[] args) {
        startClient();
    }

    private static void startClient() {
        try (DatagramSocket socket = new DatagramSocket()) {
            sendMessage("CLIENT - UDP!", socket);
            receiveResponseMessage(socket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String message, DatagramSocket socket) throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), address, UDPServer.PORT);
        socket.send(sendPacket);
        System.out.println("Message sent to: " + address.getHostAddress() + ":" + UDPServer.PORT);
    }

    private static void receiveResponseMessage(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Received message: " + message);
        System.out.println("From address: " + receivePacket.getAddress() + ":" + receivePacket.getPort());
    }
}
