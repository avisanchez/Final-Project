package core;

import java.io.IOException;
import java.io.Serializable;

import core.mydatastruct.*;
import core.gameobject.*;

public class ObjectManager implements Serializable {

    private static Texture[] textures;

    public ObjectManager() {
        textures = new Texture[9];

        // load textures
        try {
            String texturePath = "core/resource/texture/";
            String spritePath = "core/resource/sprite/";
            textures[0] = new Texture(texturePath + "bluestone.png");
            textures[1] = new Texture(texturePath + "colorstone.png");
            textures[2] = new Texture(texturePath + "eagle.png");
            textures[3] = new Texture(texturePath + "greystone.png");
            textures[4] = new Texture(texturePath + "mossy.png");
            textures[5] = new Texture(texturePath + "purplestone.png");
            textures[6] = new Texture(texturePath + "redbrick.png");
            textures[7] = new Texture(texturePath + "wood.png");
            textures[8] = new Texture(spritePath + "player.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Texture getTexture(int textureNum) {
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

}