package org.homework.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageReceiverThread extends Thread {

    private final BufferedReader in;
    private boolean running = true;

    public MessageReceiverThread(InputStream in) {
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public void run() {
        try {
            String serverResponse;
            while (running && (serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
            }
        } catch (IOException e) {
            System.out.println("[MessageReceiver] Closed!");
            running = false;
            ChatClient.notifyDisconnect();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                System.out.println("In buffer was not closed!");
            }
        }
    }

    public void end(){
        running = false;
    }
}
