package org.zadania.zad2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class JavaUdpServer {

    public static final int PORT = 8999;

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        System.out.println("UDP Server started!");
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] receiveBuffer = new byte[1024];
            while (true) {
                receiveMessageAndSendResponse(receiveBuffer, socket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void receiveMessageAndSendResponse(byte[] receiveBuffer, DatagramSocket socket) throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);

        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Received message: " + message);

        String responseMessage = "UDP Server response!";
        DatagramPacket sendPacket = new DatagramPacket(responseMessage.getBytes(), responseMessage.length(), receivePacket.getAddress(), receivePacket.getPort());
        socket.send(sendPacket);
        System.out.println("Response message sent to: " + receivePacket.getAddress() + ":" + receivePacket.getPort());
    }
}
