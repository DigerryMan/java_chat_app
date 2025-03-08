package org.homework.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastMessageReceiverThread extends Thread {

    private final MulticastSocket multicastSocket;
    private boolean running = true;

    public MulticastMessageReceiverThread(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("[Multicast] message: " + message);
            }
        } catch (IOException e) {
            System.out.println("[MulticastMessageReceiver] Closed!");
        } finally {
            multicastSocket.close();
        }
    }

    public void end() {
        running = false;
    }
}
