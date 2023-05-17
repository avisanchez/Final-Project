package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import core.mydatastructs.*;
import core.gameobjects.*;

public class ObjectManager {

    private int[][] textures;
    private Sprite[] sprites;

    public ObjectManager() {
        textures = new int[9][Settings.TEXTURE_WIDTH * Settings.TEXTURE_HEIGHT];
        sprites = new Sprite[] { new Sprite(6.5, 2.5, 8) };

        // load textures
        try {
            String texturePath = "core/resources/textures/";
            String spritePath = "core/resources/sprites/";
            loadTexture(0, new File(texturePath + "bluestone.png"));
            loadTexture(1, new File(texturePath + "colorstone.png"));
            loadTexture(2, new File(texturePath + "eagle.png"));
            loadTexture(3, new File(texturePath + "greystone.png"));
            loadTexture(4, new File(texturePath + "mossy.png"));
            loadTexture(5, new File(texturePath + "purplestone.png"));
            loadTexture(6, new File(texturePath + "redbrick.png"));
            loadTexture(7, new File(texturePath + "wood.png"));
            loadTexture(8, new File(spritePath + "player.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] getTexture(int textureNum) {
        if (textureNum < 0 || textureNum > MapManager.worldMap.length) {
            System.out.println("Error: texture " + textureNum + " not detected");
        }
        return textures[textureNum];
    }

    public Sprite[] getSortedSprites(Player player) {
        Sprite[] sortedSprites = sprites.clone();

        for (int i = 0; i < sortedSprites.length; i++) {
            for (int j = 1; j < (sortedSprites.length - i); j++) {
                Sprite currSprite = sortedSprites[j];
                Sprite nextSprite = sortedSprites[j - 1];

                double currDist = Vector3.sqrDist(player.worldPos, currSprite.worldPos);
                double nextDist = Vector3.sqrDist(player.worldPos, nextSprite.worldPos);
                if (nextDist > currDist) {
                    Sprite temp = sortedSprites[j];
                    sortedSprites[j] = sortedSprites[j + 1];
                    sortedSprites[j + 1] = temp;
                }
            }
        }

        return sortedSprites;
    }

    private void loadTexture(int index, File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelColor = image.getRGB(x, y);
                textures[index][Settings.TEXTURE_WIDTH * x + y] = pixelColor;
            }
        }

    }

}