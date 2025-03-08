package org.homework.client;

import org.homework.server.ChatServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class MessageSender {

    private final PrintWriter tcpOut;
    private final DatagramSocket udpSocket;
    private final MulticastSocket multicastSocket;
    private final InetSocketAddress multicastAddress;
    private static final String ASCII_ART = "  _____ _                 _ _   _       _ _   _              \n" +
            " / ____| |               | | \\ | |     | | | (_)             \n" +
            "| |    | | ___  _   _  __| |  \\| |_   _| | |_ _  ___  _ __   \n" +
            "| |    | |/ _ \\| | | |/ _` | . ` | | | | | __| |/ _ \\| '_ \\  \n" +
            "| |____| | (_) | |_| | (_| | |\\  | |_| | | |_| | (_) | | | | \n" +
            " \\_____|_|\\___/ \\__,_|\\__,_|_| \\_|\\__,_|_|\\__|_|\\___/|_| |_|";


    MessageSender(OutputStream tcpOutputStream, DatagramSocket udpSocket, MulticastSocket multicastSocket, InetSocketAddress multicastAddress){
        this.tcpOut = new PrintWriter(tcpOutputStream, true);
        this.udpSocket = udpSocket;
        this.multicastSocket = multicastSocket;
        this.multicastAddress = multicastAddress;
    }

    void sendMessagesFromConsole() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String userInput = "";
        while (!Objects.equals(userInput, "q") && !ChatClient.isConnectionLost()) {
            userInput = scanner.nextLine();
            if (isUdpMessage(userInput)) {
                //sendUdpMessage(userInput.substring(3));
                sendUdpMessage(ASCII_ART);
            } else if (isMulticast(userInput)) {
                sendMulticastMessage(userInput.substring(1));
            } else{
                tcpOut.println(userInput);
            }
        }
    }

    private boolean isUdpMessage(String message){
        return message.startsWith("udp");
    }
    private boolean isMulticast(String message){
        return message.startsWith("M");
    }

    private void sendUdpMessage(String message) throws IOException {
        InetAddress address = InetAddress.getByName(ChatServer.HOST);
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), address, ChatServer.PORT);
        udpSocket.send(sendPacket);
    }

    private void sendMulticastMessage(String message) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), multicastAddress.getAddress(), multicastAddress.getPort());
        multicastSocket.send(sendPacket);
    }

    public void end(){
        tcpOut.close();
    }
}
