import core.mydatastructs.*;
import core.gameobjects.*;
import core.MapManager;
import java.net.Socket;
import java.util.UUID;

public class Manager {
    private MyArrayList<ServerThread> threadList;

    public Manager() {
        threadList = new MyArrayList<>();

    }

    public void addClient(Socket client) {

        // create and start sever thread
        ServerThread connection = new ServerThread(client, this);
        threadList.add(connection);
        Thread thread = new Thread(connection);
        thread.start();

        // create player random position
        UUID id = UUID.randomUUID();
        double newPlayerX, newPlayerY;

        int x = (int) (Math.random() * MapManager.worldMap.length);
        int y = (int) (Math.random() * MapManager.worldMap.length);
        while (MapManager.worldMap[x][y] != 0) {
            x = (int) (Math.random() * MapManager.worldMap.length);
            y = (int) (Math.random() * MapManager.worldMap.length);
        }
        newPlayerX = x + 0.5;
        newPlayerY = y + 0.5;

        // send the game to the screen
        broadcastMessage(new Message(Message.Tag.CREATE_PLAYER, new Player(id, newPlayerX, newPlayerY, 8)));
        connection.sendToClient(new Message(Message.Tag.ASSIGN_PLAYER, id));

    }

    // public void removeClient(ServerThread serverThread) {
    // game.getObjectManager().destoryPlayer(threadDict.get(serverThread));

    // threadDict.remove(serverThread);

    // System.out.println("num threads: " + threadDict.size());
    // System.out.println("num players: " + threadDict.size());

    // }

    public void broadcastMessage(Message message) {
        for (ServerThread thread : threadList) {
            thread.sendToClient(message);

        }
    }
}