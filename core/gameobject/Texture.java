package core.gameobject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Texture implements Serializable {
    public final int WIDTH;
    public final int HEIGHT;

    private BufferedImage image;
    private int[] pixels;

    public Texture(String filePath) throws IOException {
        // load image
        image = ImageIO.read(new File(filePath));
        WIDTH = image.getWidth();
        HEIGHT = image.getHeight();
        image.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);

    }

    public BufferedImage getImage() {
        return image;
    }

    public int[] getPixels() {
        return pixels;
    }

}