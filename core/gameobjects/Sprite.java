package core.gameobjects;

public class Sprite extends GameObject {
    public int textureNum;

    public Sprite(double worldX, double worldY, int textureNum) {
        super(worldX, worldY);
        this.textureNum = textureNum;
    }

}