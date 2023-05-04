import java.util.ArrayList;

import java.net.Socket;

public class Manager {

    private ArrayList<ServerThread> threads;

    public Manager() {
        threads = new ArrayList<>();

    }

    public void addClient(Socket client) {
        ServerThread connection = new ServerThread(client, this);
        threads.add(connection);
        Thread thread = new Thread(connection);
        thread.start();
    }

    public void broadcastMessage(Message message) {
        for (ServerThread thread : threads) {
            thread.sendToClient(message);

        }

    }
}