package core.gameobjects;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.UUID;
import java.awt.Color;

import core.mydatastructs.Vector3;
import core.Settings;

public abstract class GameObject implements Serializable {
    public Vector3 worldPos;
    public int textureNum;
    public UUID id;

    public GameObject(UUID id, double worldX, double worldY, int textureNum) {
        worldPos = new Vector3(worldX, worldY, 0);
        this.textureNum = textureNum;
        this.id = id;
    }

    /**
     * Returns the screen position for the game object to display on the minimap
     */
    public Vector3 getScreenPos() {
        return worldPos.copy().mult(Settings.CELL_SIZE * Settings.MINIMAP_SCALE);
    }

    public void draw(Graphics g) {
        Vector3 screenPos = this.getScreenPos();

        // draw circle centered at player coordinates
        int rad = (int) (Settings.MINIMAP_SCALE * 5);
        int diam = 2 * rad;

        g.setColor(Color.red);
        g.fillOval((int) (screenPos.x - rad), (int) (screenPos.y - rad), diam, diam);
    }

    public abstract String toString();

    public abstract GameObject copy();

    /**
     * Checks for equality using UUID values
     */
    @Override
    public boolean equals(Object otherGameObject) {
        if (!(otherGameObject instanceof GameObject)) {
            return false;
        }
        return this.id.equals(((GameObject) otherGameObject).id);
    }

}
