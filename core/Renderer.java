package core;

import java.awt.Color;
import java.awt.Graphics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import core.mydatastructs.*;
import core.gameobjects.*;

public class Renderer {
    private final int NUM_THREADS = 10;

    private Player player;
    private ObjectManager objectManager;

    private double[] zBuffer;
    private int[][] pixelBuffer;

    ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    public Renderer(Player player) {
        this.player = player;
        this.objectManager = new ObjectManager();

        clearBuffers();
    }

    public void render(Graphics g, MyArrayList<Player> unsortedPlayers) {

        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        clearBuffers();

        renderFloorAndCeiling(0, Settings.SCREEN_HEIGHT);

        int pixelsPerThread = Settings.SCREEN_WIDTH / NUM_THREADS;
        for (int taskNum = 0; taskNum < NUM_THREADS; taskNum++) {
            int start = pixelsPerThread * taskNum;
            int end = start + pixelsPerThread;

            Pair<Integer, Integer> vBounds = new Pair<>(start, end);

            Task renderTask = new Task(latch, 0, this, vBounds);
            executorService.submit(renderTask);
        }

        // renderWalls(0, Settings.SCREEN_WIDTH);
        renderPlayers(unsortedPlayers);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        drawScene(g, 0, Settings.SCREEN_HEIGHT);

    }

    public void renderFloorAndCeiling(int start, int end) {
        // FLOOR CASTING

        for (int y = start; y < end; y++) {
            // rayDir for leftmost ray (x = 0) and rightmost ray (x = w)
            Vector3 leftRay = player.dir.sub(player.cameraPlane);
            Vector3 rightRay = player.dir.add(player.cameraPlane);

            // Current y position compared to the center of the screen (the horizon)
            int p = y - Settings.SCREEN_HEIGHT / 2;

            // Vertical position of the camera.
            double posZ = 0.5 * Settings.SCREEN_HEIGHT;

            // Horizontal distance from the camera to the floor for the current row.
            // 0.5 is the z position exactly in the middle between floor and ceiling.
            double rowDistance = posZ / p;

            // calculate the real world step vector we have to add for each x (parallel to
            // camera plane)
            // adding step by step avoids multiplications with a weight in the inner loop
            double floorStepX = rowDistance * (rightRay.x - leftRay.x) / Settings.SCREEN_WIDTH;
            double floorStepY = rowDistance * (rightRay.y - leftRay.y) / Settings.SCREEN_WIDTH;

            // real world coordinates of the leftmost column. This will be updated as we
            // step to the right.
            double floorX = player.worldPos.x + rowDistance * leftRay.x;
            double floorY = player.worldPos.y + rowDistance * leftRay.y;

            for (int x = 0; x < Settings.SCREEN_WIDTH; x++) {
                // the cell coord is simply got from the integer parts of floorX and floorY
                int cellX = (int) (floorX);
                int cellY = (int) (floorY);

                // get the texture coordinate from the fractional part
                int tx = (int) (Settings.TEXTURE_WIDTH * (floorX - cellX)) & (Settings.TEXTURE_WIDTH - 1);
                int ty = (int) (Settings.TEXTURE_HEIGHT * (floorY - cellY)) & (Settings.TEXTURE_HEIGHT - 1);

                floorX += floorStepX;
                floorY += floorStepY;

                // choose texture and draw the pixel
                int floorTexture = 3;
                int ceilingTexture = 6;
                int color;

                // floor
                color = objectManager.getTexture(floorTexture)[Settings.TEXTURE_WIDTH * ty + tx];
                color = (color >> 1) & 8355711; // make a bit darker
                pixelBuffer[y][x] = color;

                // ceiling (symmetrical, at screenHeight - y - 1 instead of y)
                color = objectManager.getTexture(ceilingTexture)[Settings.TEXTURE_WIDTH * ty
                        + tx];
                color = (color >> 1) & 8355711; // make a bit darker
                pixelBuffer[Settings.SCREEN_HEIGHT - y - 1][x] = 0;
            }
        }
    }

    public void renderWalls(int start, int end) {
        // cast a ray for every horizontal pixel on-screen
        for (int x = start; x < end; x++) {
            double cameraX = 2 * x / (double) Settings.SCREEN_WIDTH - 1;
            Vector3 dir = player.cameraPlane.mult(cameraX).add(player.dir);

            castRay(player.worldPos.x, player.worldPos.y, dir, x);
        }
    }

    public void drawScene(Graphics g, int start, int end) {

        for (int row = start; row < end; row++) {
            for (int col = 0; col < pixelBuffer[0].length; col++) {
                if (pixelBuffer[row][col] == 0) { // pixel is black
                    continue;
                }

                g.setColor(new Color(pixelBuffer[row][col]));
                g.fillRect(col, row, 1, 1);
            }
        }

    }

    private void renderPlayers(MyArrayList<Player> sortedObjects) {

        for (Player gameObject : sortedObjects) {

            double spriteX = gameObject.worldPos.x - player.worldPos.x;
            double spriteY = gameObject.worldPos.y - player.worldPos.y;

            double invDet = 1.0 / (player.cameraPlane.x * player.dir.y - player.dir.x *
                    player.cameraPlane.y);
            double transformX = invDet * (player.dir.y * spriteX - player.dir.x *
                    spriteY);
            double transformY = invDet * (-player.cameraPlane.y * spriteX +
                    player.cameraPlane.x * spriteY);

            int spriteScreenX = (int) ((Settings.SCREEN_WIDTH / 2) * (1 + transformX /
                    transformY));

            // shift sprite down
            int vMove = 150;
            int vMoveScreen = (int) (vMove / transformY);

            // calculate height of the sprite on screen
            int spriteHeight = Math.abs((int) (Settings.SCREEN_HEIGHT / transformY));
            // calculate lowest and highest pixel to fill in current stripe
            int drawStartY = -spriteHeight / 2 + Settings.SCREEN_HEIGHT / 2 +
                    vMoveScreen;
            if (drawStartY < 0) {
                drawStartY = 0;
            }
            int drawEndY = spriteHeight / 2 + Settings.SCREEN_HEIGHT / 2 + vMoveScreen;
            if (drawEndY >= Settings.SCREEN_HEIGHT) {
                drawEndY = Settings.SCREEN_HEIGHT - 1;
            }

            // calculate width of the sprite
            int spriteWidth = Math.abs((int) (Settings.SCREEN_HEIGHT / transformY));
            int drawStartX = -spriteWidth / 2 + spriteScreenX;
            if (drawStartX < 0) {
                drawStartX = 0;
            }
            int drawEndX = spriteWidth / 2 + spriteScreenX;
            if (drawEndX >= Settings.SCREEN_WIDTH) {
                drawEndX = Settings.SCREEN_WIDTH - 1;
            }

            // loop through every vertical stripe of the sprite on screen
            for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                int texX = ((int) (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) *
                        Settings.TEXTURE_WIDTH
                        / spriteWidth)) / 256;

                if (transformY > 0 && stripe > 0 && stripe < Settings.SCREEN_WIDTH &&
                        transformY < zBuffer[stripe])
                    for (int y = drawStartY; y < drawEndY; y++) // for every pixel of the current stripe
                    {
                        int d = (y - vMoveScreen) * 256 - Settings.SCREEN_HEIGHT * 128 + spriteHeight
                                * 128;
                        int texY = ((d * Settings.TEXTURE_HEIGHT) / spriteHeight) / 256;

                        int color = objectManager.getTexture(gameObject.textureNum)[Settings.TEXTURE_WIDTH * texX
                                + texY];

                        if ((color & 0x00FFFFFF) != 0)
                            pixelBuffer[y][stripe] = color;
                    }
            }
        }
    }

    /**
     * Cast ray from current player position given initial angle
     * 
     * Using Lodes's implementation of DDA algorithm
     * (https://lodev.org/cgtutor/raycasting.html)
     * 
     * @param rayAngle the angle of the ray in radians
     */
    private void castRay(double startX, double startY, Vector3 dir, int x) {

        // world coordinates of ray starting position
        Vector3 rayOrigin = new Vector3(startX, startY, 0);

        // normalized ray direction
        Vector3 rayDir = dir;

        // ray unit step size
        Vector3 unitStepSize = new Vector3(Math.abs(1.0f / rayDir.x), Math.abs(1.0f / rayDir.y), 0);

        // x and y direction algorithm will step in
        Vector3 step = new Vector3();

        // current length of ray
        Vector3 rayLength = new Vector3();

        // truncated map coordinates
        Vector3 mapCheck = new Vector3((int) rayOrigin.x, (int) rayOrigin.y, 0);

        // starting conditions
        if (rayDir.x > 0) {
            step.x = 1;
            rayLength.x = (mapCheck.x + 1 - rayOrigin.x) * unitStepSize.x;

        } else {
            step.x = -1;
            rayLength.x = (rayOrigin.x - mapCheck.x) * unitStepSize.x;

        }

        if (rayDir.y > 0) {
            step.y = 1;
            rayLength.y = (mapCheck.y + 1 - rayOrigin.y) * unitStepSize.y;

        } else {
            step.y = -1;
            rayLength.y = (rayOrigin.y - mapCheck.y) * unitStepSize.y;

        }

        // run algorithm
        int VERTICAL_WALL = 1;
        int HORIZONTAL_WALL = 0;

        boolean hitDetected = false;
        double maxDist = 100; // max distance algorithm will check
        double worldDistToHit = 0;
        int side = -1;

        while (!hitDetected && worldDistToHit < maxDist) {

            if (rayLength.x < rayLength.y) {
                mapCheck.x += step.x;
                worldDistToHit = rayLength.x;
                rayLength.x += unitStepSize.x;
                side = HORIZONTAL_WALL;

            } else {
                mapCheck.y += step.y;
                worldDistToHit = rayLength.y;
                rayLength.y += unitStepSize.y;
                side = VERTICAL_WALL;

            }

            int row = (int) mapCheck.y;
            int col = (int) mapCheck.x;

            if (col >= 0 && col < MapManager.worldMap[0].length && row >= 0 && row < MapManager.worldMap.length) {
                if (MapManager.worldMap[row][col] >= 1) {
                    hitDetected = true;

                }
            }
        }

        // guard against rendering squares that don't exist
        if (!hitDetected) {
            return;
        }

        // fix fisheye effect
        double perpWallDist;
        if (side == HORIZONTAL_WALL) {
            perpWallDist = rayLength.x - unitStepSize.x;
        } else {
            perpWallDist = rayLength.y - unitStepSize.y;
        }

        int lineHeight = (int) (Settings.SCREEN_HEIGHT / perpWallDist);

        int drawStart = (Settings.SCREEN_HEIGHT - lineHeight) / 2;
        if (drawStart < 0) {
            drawStart = 0;
        }

        int drawEnd = (Settings.SCREEN_HEIGHT + lineHeight) / 2;
        if (drawEnd >= Settings.SCREEN_HEIGHT) {
            drawEnd = Settings.SCREEN_HEIGHT - 1;
        }

        // RENDER WALLS
        int textureNum = MapManager.worldMap[(int) mapCheck.y][(int) mapCheck.x] - 1;

        double wallOffset;
        if (side == HORIZONTAL_WALL) {
            wallOffset = rayOrigin.y + perpWallDist * rayDir.y;
        } else {
            wallOffset = rayOrigin.x + perpWallDist * rayDir.x;
        }
        wallOffset -= (int) wallOffset;

        // get x position on texture
        int textureX = (int) (wallOffset * (double) Settings.TEXTURE_WIDTH);
        if (side == 0 && rayDir.x > 0)
            textureX = Settings.TEXTURE_WIDTH - textureX - 1;
        if (side == 1 && rayDir.y < 0)
            textureX = Settings.TEXTURE_WIDTH - textureX - 1;

        // loop through y values downward
        double vertStep = 1.0 * Settings.TEXTURE_HEIGHT / lineHeight;
        double texturePos = (drawStart - Settings.SCREEN_HEIGHT / 2 + lineHeight / 2)
                *
                vertStep;
        for (int y = drawStart; y < drawEnd; y++) {
            int textureY = (int) texturePos & (Settings.TEXTURE_HEIGHT - 1);
            texturePos += vertStep;
            int color = objectManager.getTexture(textureNum)[Settings.TEXTURE_WIDTH * textureX + textureY];
            if (side == VERTICAL_WALL) {
                color = (color >> 1) & 8355711; // shading magic
            }
            pixelBuffer[y][x] = color;
        }
        zBuffer[x] = perpWallDist;

    }

    private void clearBuffers() {
        pixelBuffer = new int[Settings.SCREEN_HEIGHT][Settings.SCREEN_WIDTH];
        zBuffer = new double[Settings.SCREEN_WIDTH];
    }
}

class Task implements Runnable {
    private Renderer renderer;
    private Pair<Integer, Integer> bounds;
    private CountDownLatch latch;
    private int taskType;

    public Task(CountDownLatch latch, int taskType, Renderer renderer, Pair<Integer, Integer> bounds) {
        this.latch = latch;
        this.renderer = renderer;
        this.bounds = bounds;
        this.taskType = taskType;
    }

    @Override
    public void run() {
        renderer.renderWalls(bounds._0, bounds._1);
        latch.countDown();
    }

}
