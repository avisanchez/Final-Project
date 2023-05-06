import mydatastructs.*;

public class Player {
    public Vector3 worldPos;
    public double angle;

    public int radius2D;

    public Player(double worldX, double worldY) {
        worldPos = new Vector3(worldX, worldY, 0);
        angle = Math.PI / 2;
        radius2D = 5;
    }

    /**
     * Rotates player angle by a certain number of radians
     * 
     * @param rads the number of radians to turn the player by
     * 
     */
    public void turn(double rads) {
        angle += rads;
        if (angle < 0) {
            angle = 2 * Math.PI + angle;
        }
        angle %= 2 * Math.PI;
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
        double dx = stepSize * Math.cos(angle);
        double dy = stepSize * Math.sin(angle);

        worldPos.x += dx;
        worldPos.y -= dy;
    }
}