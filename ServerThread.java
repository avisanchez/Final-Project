import java.net.*;
import java.io.*;

import core.mydatastructs.*;

public class ServerThread implements Runnable {
    private Manager manager;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ServerThread(Socket client, Manager manager) {
        this.manager = manager;
        try {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + ": connection opened.");

        // Receive new messages from client
        try {
            while (true) {

                if (Thread.interrupted()) {
                    break;
                }

                Message message = (Message) in.readObject();

                switch (message.tag) {
                    case UPDATE_PLAYER:
                        manager.broadcastMessage(message);
                        break;

                    default:
                        break;
                }
            }

        } catch (IOException ex) {
            System.out.println("ERROR: Failed to get message from cliet.");
            ex.printStackTrace();

        } catch (ClassNotFoundException ex) {
            System.out.println("ERROR: Failed to identify class from client.");
            ex.printStackTrace();

        }

        System.out.println(Thread.currentThread().getName() + ": connection closed.");

        // close streams
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends the current message to a client
     * 
     * @param message the message to be sent
     * 
     */
    public void sendToClient(Message message) {
        try {
            out.reset();
            out.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
