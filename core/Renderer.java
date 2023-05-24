package core;

import java.awt.Color;
import java.awt.Graphics;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import core.mydatastruct.*;
import core.gameobject.*;

public class Renderer {
    private final int NUM_THREADS = 10;

    private Player player;
    private ObjectManager objectManager;

    private double[] zBuffer;

    private ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
    private CountDownLatch latch;

    public Renderer(Player player) {
        this.player = player;
        this.objectManager = new ObjectManager();

        clearBuffers();
    }

    public void render(Graphics g, MyArrayList<Player> unsortedPlayers) {

        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        latch = new CountDownLatch(NUM_THREADS);

        clearBuffers();

        // // multithread drawing
        // int pixelsPerThread = Settings.SCREEN_WIDTH / NUM_THREADS;
        // for (int taskNum = 0; taskNum < NUM_THREADS; taskNum++) {
        // int start = pixelsPerThread * taskNum;
        // int end = start + pixelsPerThread;

        // Pair<Integer, Integer> vBounds = new Pair<>(start, end);

        // Task renderTask = new Task(g, latch, 0, this, vBounds);
        // executorService.submit(renderTask);
        // }

        // try {
        // latch.await();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // executorService.shutdown();

        // regular single-thread drawing
        renderWalls(g, 0, Settings.SCREEN_WIDTH);
        renderPlayers(g, objectManager.getSortedPlayers(player, unsortedPlayers));
    }

    public void renderWalls(Graphics g, int start, int end) {
        // cast a ray for every horizontal pixel on-screen
        for (int x = start; x < end; x++) {
            double cameraX = 2 * x / (double) Settings.SCREEN_WIDTH - 1;
            Vector3 dir = player.cameraPlane.mult(cameraX).add(player.dir);

            castRay(g, player.worldPos.x, player.worldPos.y, dir, x);
        }
    }

    private void renderPlayers(Graphics g, MyArrayList<Player> sortedObjects) {

        for (Player gameObject : sortedObjects) {

            double spriteWorldX = gameObject.worldPos.x - player.worldPos.x;
            double spriteWorldY = gameObject.worldPos.y - player.worldPos.y;

            double invDet = 1.0 / (player.cameraPlane.x * player.dir.y - player.dir.x *
                    player.cameraPlane.y);
            double transformX = invDet * (player.dir.y * spriteWorldX - player.dir.x *
                    spriteWorldY);
            double transformY = invDet * (-player.cameraPlane.y * spriteWorldX +
                    player.cameraPlane.x * spriteWorldY);

            int spriteScreenX = (int) ((Settings.SCREEN_WIDTH / 2) * (1 + transformX /
                    transformY));

            // shift sprite down
            int vMove = 150;
            int vMoveScreen = (int) (vMove / transformY);

            // calculate height of the sprite on screen
            int spriteHeight = Math.abs((int) (Settings.SCREEN_HEIGHT / transformY));

            // calculate width of the sprite
            int spriteWidth = Math.abs((int) (Settings.SCREEN_HEIGHT / transformY));

            int drawStartX = spriteScreenX - spriteWidth / 2;
            int drawEndX = spriteScreenX + spriteWidth / 2;

            int drawStartY = (Settings.SCREEN_HEIGHT - spriteHeight) / 2 + vMoveScreen;
            int drawEndY = spriteHeight / 2 + Settings.SCREEN_HEIGHT / 2 + vMoveScreen;

            // loop through every vertical stripe of the sprite on screen
            for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                int texX = (int) ((stripe - spriteScreenX + spriteWidth / 2) * Settings.TEXTURE_WIDTH / spriteWidth);

                if (transformY > 0 && stripe > 0 && stripe < Settings.SCREEN_WIDTH && transformY < zBuffer[stripe]) {
                    // draw textured line
                    g.drawImage(objectManager.getTexture(8).getImage(), stripe, drawStartY, stripe + 1, drawEndY, texX,
                            0,
                            texX + 1, Settings.TEXTURE_HEIGHT, null);
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
    private void castRay(Graphics g, double startX, double startY, Vector3 dir, int x) {

        // world coordinates of ray starting position
        Vector3 rayOrigin = new Vector3(startX, startY, 0);

        // normalized ray direction
        Vector3 rayDir = dir;

        // ray unit step size
        Vector3 unitStepSize = new Vector3(Math.abs(1.0 / rayDir.x), Math.abs(1.0 / rayDir.y), 0);

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
        // 0 = horizontal wall, 1 = vetical wall

        boolean hitDetected = false;
        int maxIterations = 100; // max distance algorithm will check
        int numIterations = 0;
        int side = -1;

        while (!hitDetected && numIterations < maxIterations) {

            if (rayLength.x < rayLength.y) {
                mapCheck.x += step.x;
                rayLength.x += unitStepSize.x;
                side = 0;

            } else {
                mapCheck.y += step.y;
                rayLength.y += unitStepSize.y;
                side = 1;

            }

            int row = (int) mapCheck.y;
            int col = (int) mapCheck.x;

            if (col >= 0 && col < MapManager.worldMap[0].length && row >= 0 && row < MapManager.worldMap.length) {
                if (MapManager.worldMap[row][col] >= 1) {
                    hitDetected = true;

                }
            }
            numIterations++;
        }

        // guard against rendering squares that don't exist
        if (!hitDetected) {
            return;
        }

        // fix fisheye effect
        double perpWallDist;
        if (side == 0) {
            perpWallDist = rayLength.x - unitStepSize.x;
        } else {
            perpWallDist = rayLength.y - unitStepSize.y;
        }
        zBuffer[x] = perpWallDist;

        int lineHeight = (int) (Settings.SCREEN_HEIGHT / perpWallDist);

        int drawStart = (Settings.SCREEN_HEIGHT - lineHeight) / 2;

        int drawEnd = (Settings.SCREEN_HEIGHT + lineHeight) / 2;

        // RENDER WALLS
        int textureNum = MapManager.worldMap[(int) mapCheck.y][(int) mapCheck.x] - 1;

        double wallOffset;
        if (side == 0) {
            wallOffset = rayOrigin.y + perpWallDist * rayDir.y;
        } else {
            wallOffset = rayOrigin.x + perpWallDist * rayDir.x;
        }
        wallOffset -= (int) wallOffset;

        // get x position on texture
        int textureX = (int) (wallOffset * Settings.TEXTURE_WIDTH);
        if ((side == 0 && rayDir.x > 0) || (side == 1 && rayDir.y < 0)) {
            textureX = Settings.TEXTURE_WIDTH - textureX - 1;
        }

        // draw textured dline
        g.drawImage(objectManager.getTexture(textureNum).getImage(), x, drawStart, x + 1, drawEnd, textureX, 0,
                textureX + 1,
                Settings.TEXTURE_HEIGHT, null);

    }

    private void clearBuffers() {
        zBuffer = new double[Settings.SCREEN_WIDTH];
    }
}

class Task implements Runnable {
    private Renderer renderer;
    private Pair<Integer, Integer> bounds;
    private CountDownLatch latch;
    private int taskType;
    private Graphics g;

    public Task(Graphics g, CountDownLatch latch, int taskType, Renderer renderer, Pair<Integer, Integer> bounds) {
        this.g = g;
        this.latch = latch;
        this.renderer = renderer;
        this.bounds = bounds;
        this.taskType = taskType;

    }

    @Override
    public void run() {
        renderer.renderWalls(g, bounds._0, bounds._1);
        latch.countDown();
    }

}
