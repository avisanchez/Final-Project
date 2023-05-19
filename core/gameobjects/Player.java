package core.gameobjects;

import java.awt.Graphics;
import java.util.UUID;

import core.mydatastructs.Vector3;
import core.Settings;

public class Player extends GameObject {
    public Vector3 dir;
    public Vector3 cameraPlane;

    public Player(UUID id, double worldX, double worldY, int textureNum) {
        super(id, worldX, worldY, textureNum);

        dir = new Vector3(-1, 0, 0);
        cameraPlane = new Vector3(0, 0.66, 0);
    }

    private Player(UUID id, Vector3 worldPos, Vector3 dir, Vector3 cameraPlane, int textureNum) {
        super(id, worldPos.x, worldPos.y, textureNum);
        this.dir = dir;
        this.cameraPlane = cameraPlane;
    }

    /**
     * Rotates player direction and camera plane by rotation matrix
     * 
     * @param rads the number of radians to turn the player by
     * 
     */
    public void turn(double rads) {
        double newDirX = dir.x * Math.cos(rads) - dir.y * Math.sin(rads);
        double newDirY = dir.x * Math.sin(rads) + dir.y * Math.cos(rads);
        dir = new Vector3(newDirX, newDirY, 0);

        double newCameraPlaneX = cameraPlane.x * Math.cos(rads) - cameraPlane.y * Math.sin(rads);
        double newCameraPlaneY = cameraPlane.x * Math.sin(rads) + cameraPlane.y * Math.cos(rads);
        cameraPlane = new Vector3(newCameraPlaneX, newCameraPlaneY, 0);

    }

    /**
     * Moves player along current heading
     * 
     * @param stepSize the size of the player step in world coordinates. Inputing a
     *                 negative number will move the player backwards.
     * 
     */
    public void move(double stepSize) {
        worldPos.x += stepSize * dir.norm().x;
        worldPos.y += stepSize * dir.norm().y;
    }

    public void drawFOV(Graphics g) {
        Vector3 screenPos = this.getScreenPos();

        int len = (int) (30 * Settings.MINIMAP_SCALE);

        Vector3 leftPoint, rightPoint;
        leftPoint = new Vector3();
        rightPoint = new Vector3();

        leftPoint.x = screenPos.x + len * (dir.x + cameraPlane.x);
        leftPoint.y = screenPos.y + len * (dir.y + cameraPlane.y);
        rightPoint.x = screenPos.x + len * (dir.x - cameraPlane.x);
        rightPoint.y = screenPos.y + len * (dir.y - cameraPlane.y);

        // g.drawLine((int) leftPoint.x, (int) leftPoint.y, (int) screenPos.x, (int)
        // screenPos.y);
        // g.drawLine((int) rightPoint.x, (int) rightPoint.y, (int) screenPos.x, (int)
        // screenPos.y);

        Vector3.drawLine2D(g, screenPos, leftPoint);
        Vector3.drawLine2D(g, screenPos, rightPoint);
    }

    @Override
    public String toString() {
        return "Player: " + worldPos.toString(2);
    }

    @Override
    public Player copy() {
        return new Player(id, worldPos.copy(), dir.copy(), cameraPlane.copy(), textureNum);
    }
}