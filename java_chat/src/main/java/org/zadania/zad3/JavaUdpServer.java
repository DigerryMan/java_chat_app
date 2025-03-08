package org.zadania.zad3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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

        ByteBuffer wrapped = ByteBuffer.wrap(receivePacket.getData()).order(ByteOrder.LITTLE_ENDIAN);
        int receivedNumber = wrapped.getInt();
        System.out.println("LittleEndian Number: " + receivedNumber);

        byte[] responseBuffer = ByteBuffer.allocate(4)
                                        .order(ByteOrder.LITTLE_ENDIAN)
                                        .putInt(++receivedNumber)
                                        .array();

        DatagramPacket sendPacket = new DatagramPacket(responseBuffer,
                responseBuffer.length, receivePacket.getAddress(), receivePacket.getPort());

        socket.send(sendPacket);
        System.out.println("Response message sent to: " + receivePacket.getAddress() + ":" + receivePacket.getPort());
    }
}
