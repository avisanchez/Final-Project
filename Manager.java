import mydatastructs.*;
import java.net.Socket;

public class Manager {

    private MyArrayList<ServerThread> threads;

    public Manager() {
        threads = new MyArrayList<ServerThread>();

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