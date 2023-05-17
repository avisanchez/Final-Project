package core.gameobjects;

import java.awt.Graphics;
import core.mydatastructs.Vector3;
import core.Settings;

public class Player extends GameObject {
    public Vector3 dir;
    public Vector3 cameraPlane;

    public Player(double worldX, double worldY) {
        super(worldX, worldY);

        dir = new Vector3(-1, 0, 0);
        cameraPlane = new Vector3(0, 0.66, 0);
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

        int len = (int) (10 * Settings.MINIMAP_SCALE);

        Vector3 leftPoint, rightPoint;
        leftPoint = rightPoint = new Vector3();

        leftPoint.x = screenPos.x + len * (dir.x + cameraPlane.x);
        leftPoint.y = screenPos.y + len * (dir.y + cameraPlane.y);
        rightPoint.x = screenPos.x + len * (dir.x - cameraPlane.x);
        rightPoint.y = screenPos.y + len * (dir.y - cameraPlane.y);

        Vector3.drawLine2D(g, screenPos, leftPoint);
        Vector3.drawLine2D(g, screenPos, rightPoint);
    }
}