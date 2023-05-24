import java.net.Socket;

import core.mydatastruct.*;
import core.gameobject.*;
import core.MapManager;

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
        int worldX = (int) (Math.random() * MapManager.worldMap.length);
        int worldY = (int) (Math.random() * MapManager.worldMap.length);

        while (MapManager.worldMap[worldY][worldX] > 0) {
            worldX = (int) (Math.random() * MapManager.worldMap.length);
            worldY = (int) (Math.random() * MapManager.worldMap.length);
        }
        worldX += 0.5;
        worldY += 0.5;

        broadcastMessage(new Message(Message.Tag.CREATE_PLAYER, new Player(id, worldX, worldY, 8)));
        connection.sendToClient(new Message(Message.Tag.ASSIGN_PLAYER, id));

    }

    public void removeClient(ServerThread serverThread) {
        threadList.remove(serverThread);
    }

    public void broadcastMessage(Message message) {
        for (ServerThread thread : threadList) {
            thread.sendToClient(message);

        }
    }
}