package org.homework.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastMessageReceiverThread extends Thread {

    private final MulticastSocket multicastSocket;
    private final String multicastUUID;

    public MulticastMessageReceiverThread(MulticastSocket multicastSocket, String multicastUUID) {
        this.multicastSocket = multicastSocket;
        this.multicastUUID = multicastUUID;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                String fullMessage = new String(packet.getData(), 0, packet.getLength());
                String[] messageParts = fullMessage.split(" ", 2);
                if(isNotAMessageFromOurselves(messageParts)) {
                    String messageContent = messageParts[1];
                    System.out.println("[Multicast] message: " + messageContent);
                }
            }
        } catch (IOException e) {
            System.out.println("[MulticastMessageReceiver] Closed!");
        } finally {
            multicastSocket.close();
        }
    }

    private boolean isNotAMessageFromOurselves(String[] parts) {
        return !parts[0].equals(multicastUUID);
    }
}
