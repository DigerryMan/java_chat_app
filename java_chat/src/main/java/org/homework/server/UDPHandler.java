package org.homework.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPHandler extends Thread{

        private final DatagramSocket socket;
        private final MessageSender messageSender;
        private boolean running = true;

        public UDPHandler(DatagramSocket socket, MessageSender messageSender) {
            this.socket = socket;
            this.messageSender = messageSender;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[1024];
                while (running) {
                    receiveMessageAndSendResponse(buffer, socket);
                }
            } catch (IOException e) {
                System.out.println("[UDPHandler] Closed!");
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
            System.out.println("Received UDP message: \n" + message);

            messageSender.udpBroadcast(udpSocket, message, receivedPacket);
        }
}
