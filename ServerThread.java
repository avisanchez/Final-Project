import java.net.*;
import java.io.*;

import java.util.UUID;
import core.mydatastruct.Message;

public class ServerThread implements Runnable {
    private Manager manager;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private UUID id;

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
        manager.removeClient(this);

        // close streams
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // delete player
        manager.broadcastMessage(new Message(Message.Tag.DELETE_PLAYER, id));

    }

    /**
     * Sends the current message to a client
     * 
     * @param message the message to be sent
     * 
     */
    public void sendToClient(Message message) {
        try {

            if (message.tag == Message.Tag.ASSIGN_PLAYER) {
                id = (UUID) message.getData();
            }

            out.reset();
            out.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
