package core.gameobject;

import java.io.Serializable;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Robot;
import java.util.UUID;

import core.mydatastruct.Vector3;
import core.Settings;

public class Player extends GameObject implements Serializable {
    private int moveDir, turnDir;

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

    public void update(double deltaTime) {
        if (moveDir != 0) {
            double moveAmount = moveDir * Settings.MOVE_SPEED * deltaTime;
            move(moveAmount);
        }

        if (turnDir != 0) {
            double turnAmount = turnDir * Settings.TURN_SPEED * deltaTime;
            turn(turnAmount);
        }
    }

    /**
     * Rotates player direction and camera plane by rotation matrix
     * 
     * @param rads the number of radians to turn the player by
     * 
     */
    private void turn(double angle) {

        double newDirX = dir.x * Math.cos(angle) - dir.y * Math.sin(angle);
        double newDirY = dir.x * Math.sin(angle) + dir.y * Math.cos(angle);
        dir = new Vector3(newDirX, newDirY, 0);

        double newCameraPlaneX = cameraPlane.x * Math.cos(angle) - cameraPlane.y * Math.sin(angle);
        double newCameraPlaneY = cameraPlane.x * Math.sin(angle) + cameraPlane.y * Math.cos(angle);
        cameraPlane = new Vector3(newCameraPlaneX, newCameraPlaneY, 0);

    }

    /**
     * Moves player along current heading
     * 
     * @param stepSize the size of the player step in world coordinates. Inputing a
     *                 negative number will move the player backwards.
     * 
     */
    private void move(double amount) {
        worldPos.x += amount * dir.norm().x;
        worldPos.y += amount * dir.norm().y;
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

        Vector3.drawLine2D(g, screenPos, leftPoint);
        Vector3.drawLine2D(g, screenPos, rightPoint);
    }

    public void setTurnDir(int turnDir) {
        this.turnDir = turnDir;
    }

    public void setMoveDir(int moveDir) {
        this.moveDir = moveDir;
    }

    /**
     * Indicates whether the player is currently moving or rotating
     * 
     * @return {@code true} if the player is moving or rotating
     */
    public boolean inMotion() {
        return (moveDir != 0) || (turnDir != 0);
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