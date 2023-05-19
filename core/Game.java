package core;

import java.io.Serializable;

public class Game implements Serializable {
    private ObjectManager objectManager;
    public MapManager mapManager;

    public Game() {
        objectManager = new ObjectManager();
        mapManager = new MapManager();

    }

    public ObjectManager getObjectManager() {
        return this.objectManager;
    }

}