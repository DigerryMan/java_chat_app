package org.homework.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpMessageReceiverThread extends Thread {

    private final DatagramSocket socket;
    private boolean running = true;

    public UdpMessageReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while (running) {
                receiveMessageAndSendResponse(buffer, socket);
            }
        } catch (IOException e) {
            System.out.println("[UDPMessageReceiver] Closed!");
        } finally {
            socket.close();
        }
    }

    public void end(){
        running = false;
    }

    private void receiveMessageAndSendResponse(byte[] receiveBuffer, DatagramSocket udpSocket) throws IOException {
        DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        udpSocket.receive(receivedPacket);

        String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
        System.out.println("Received UDP message");
        System.out.println(message);
    }
}
