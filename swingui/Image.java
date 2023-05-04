package swingui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

public class Image implements Component {

    private BufferedImage self;

    private int x, y;

    private Container container;

    public Image(String fileName) {
        try {
            self = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.out.println("Error reading image file: ");
            e.printStackTrace();
        }
    }

    public void rotateImage(int degree) {
        int width = self.getWidth(null);
        int height = self.getHeight(null);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.rotate(Math.toRadians(degree), width / 2, height / 2);
        g2d.drawImage(self, 0, 0, null);

        self = newImage;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return self.getWidth();
    }

    @Override
    public int getHeight() {
        return self.getHeight();
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public void onClick(Action actionToPerform) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onClick'");
    }

    public void updateContainerSize() {
        if (container == null) {
            return;
        }

        container.updateSize(this);
    }

    @Override
    public void setContainer(Container c) {
        container = c;
    }

}