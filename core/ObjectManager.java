package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import core.mydatastructs.*;
import core.gameobjects.*;

public class ObjectManager implements Serializable {

    private static int[][] textures;

    public ObjectManager() {
        textures = new int[9][Settings.TEXTURE_WIDTH * Settings.TEXTURE_HEIGHT];

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
        if (textureNum < 0 || textureNum > textures.length) {
            System.out.println("Error: texture " + textureNum + " not detected");
        }
        return textures[textureNum];
    }

    public MyArrayList<Player> getSortedPlayers(Player player, MyArrayList<Player> otherPlayers) {
        MyArrayList<Player> sortedPlayer = otherPlayers.copy();

        // sort the other players relative to this one
        for (int i = 0; i < sortedPlayer.size() - 1; i++) {
            for (int j = 0; j < sortedPlayer.size() - i - 1; j++) {
                GameObject currSprite = sortedPlayer.get(j);
                GameObject nextSprite = sortedPlayer.get(j + 1);

                double currDist = Vector3.sqrDist(player.worldPos, currSprite.worldPos);
                double nextDist = Vector3.sqrDist(player.worldPos, nextSprite.worldPos);
                if (nextDist > currDist) {
                    sortedPlayer.swap(j, j + 1);
                }
            }
        }

        return sortedPlayer;
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