import mydatastructs.*;

public class Player {
    public Vector3 worldPos;
    public Vector3 dir;
    public Vector3 cameraPlane;

    public Player(double worldX, double worldY) {
        worldPos = new Vector3(worldX, worldY, 0);
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
     *                 negative
     *                 number will move the player backwards.
     * 
     */
    public void move(double stepSize) {
        worldPos.x += stepSize * dir.norm().x;
        worldPos.y += stepSize * dir.norm().y;
    }
}