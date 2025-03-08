package org.zadania.zad4;

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

        sendMessage(socket, receivePacket);

    }

    private static void sendMessage(DatagramSocket socket, DatagramPacket receivePacket) throws IOException {

        String responseMessage = getResponseMessage(receivePacket);
        DatagramPacket sendPacket = new DatagramPacket(responseMessage.getBytes(), responseMessage.length(), receivePacket.getAddress(), receivePacket.getPort());
        socket.send(sendPacket);
        System.out.println("Response message sent to: " + receivePacket.getAddress() + ":" + receivePacket.getPort());
    }

    private static String getResponseMessage(DatagramPacket receivePacket) {
        if (receivePacket.getPort() <= 16000 && receivePacket.getPort() >= 15000) {
            return "Pong Java!";
        } else {
            return "Pong Python!";
        }
    }
}
