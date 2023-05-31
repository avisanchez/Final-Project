package core;

import java.io.Serializable;

public class Settings implements Serializable {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

    public static final double MINIMAP_SCALE = 0.25;
    public static final int CELL_SIZE = 32;

    public static final double MOVE_SPEED = 3;
    public static final double TURN_SPEED = 2.5;

    public static final double GAME_DURATION_MIN = 2 * 60000;

}