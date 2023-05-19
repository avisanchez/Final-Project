package core.gameobjects;

import java.util.UUID;

public class Sprite extends GameObject {
    public Sprite(UUID id, double worldX, double worldY, int textureNum) {
        super(id, worldX, worldY, textureNum);
    }

    @Override
    public String toString() {
        return "Sprite: " + worldPos.toString(2);
    }

    @Override
    public Sprite copy() {
        return new Sprite(id, worldPos.x, worldPos.y, textureNum);
    }

}