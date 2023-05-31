package core.gameobject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import core.Settings;

public class Texture implements Serializable {
    public final int WIDTH;
    public final int HEIGHT;

    private BufferedImage image;
    private int[] pixels;

    public Texture(String filePath) {
        pixels = new int[Settings.SCREEN_WIDTH * Settings.SCREEN_HEIGHT];
        // load image
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();
        pixels = image.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);

    }

    public BufferedImage getImage() {
        return image;
    }

    public int[] getPixels() {
        return pixels;
    }

}