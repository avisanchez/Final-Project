import javax.imageio.ImageIO;
import javax.management.ListenerNotFoundException;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import mydatastructs.*;

public class Screen extends JPanel implements ActionListener, KeyListener {

    // CONSTANTS
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;
    private final int TEXTURE_WIDTH = 64;
    private final int TEXTURE_HEIGHT = 64;

    private final double MINIMAP_SCALE = 0.75;
    private final int CELL_SIZE = 32;

    public final int[][] worldMap = new int[][] {
            { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 7, 7, 7, 7, 7, 7, 7, 7 },
            { 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 7 },
            { 4, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7 },
            { 4, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7 },
            { 4, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 7 },
            { 4, 0, 4, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 0, 7, 7, 7, 7, 7 },
            { 4, 0, 5, 0, 0, 0, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 7, 0, 0, 0, 7, 7, 7, 1 },
            { 4, 0, 6, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 5, 7, 0, 0, 0, 0, 0, 0, 8 },
            { 4, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 1 },
            { 4, 0, 8, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 5, 7, 0, 0, 0, 0, 0, 0, 8 },
            { 4, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 5, 7, 0, 0, 0, 7, 7, 7, 1 },
            { 4, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 0, 5, 5, 5, 5, 7, 7, 7, 7, 7, 7, 7, 1 },
            { 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 },
            { 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4 },
            { 6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 },
            { 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 6, 0, 6, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3 },
            { 4, 0, 0, 0, 0, 0, 0, 0, 0, 4, 6, 0, 6, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2 },
            { 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 2, 0, 0, 5, 0, 0, 2, 0, 0, 0, 2 },
            { 4, 0, 0, 0, 0, 0, 0, 0, 0, 4, 6, 0, 6, 2, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2 },
            { 4, 0, 6, 0, 6, 0, 0, 0, 0, 4, 6, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 2 },
            { 4, 0, 0, 5, 0, 0, 0, 0, 0, 4, 6, 0, 6, 2, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2 },
            { 4, 0, 6, 0, 6, 0, 0, 0, 0, 4, 6, 0, 6, 2, 0, 0, 5, 0, 0, 2, 0, 0, 0, 2 },
            { 4, 0, 0, 0, 0, 0, 0, 0, 0, 4, 6, 0, 6, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2 },
            { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3 }
    };
    private int[][] pixleBuffer = new int[SCREEN_HEIGHT][SCREEN_WIDTH];
    private int[][] textures = new int[8][TEXTURE_WIDTH * TEXTURE_HEIGHT];

    private Player player;

    private JFrame myFrame;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JTextField usernameField;
    private JButton enterGameButton;

    private String username;

    public Screen(Socket server, JFrame frame) {
        this.myFrame = frame;

        // create player
        player = new Player(1.5, 1.5);

        // Connect to server
        try {
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());

        } catch (IOException e) {
            System.out.println("ERROR: Failed to get server input and output streams.");
            e.printStackTrace();

        }

        // Set up components
        usernameField = new JTextField("Enter Username");
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameField);

        enterGameButton = new JButton("Ready to Play");
        enterGameButton.addActionListener(this);
        enterGameButton.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterGameButton);

        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        // load textures
        try {
            String path = "images/";
            loadTexture(0, new File(path + "bluestone.png"));
            loadTexture(1, new File(path + "colorstone.png"));
            loadTexture(2, new File(path + "eagle.png"));
            loadTexture(3, new File(path + "greystone.png"));
            loadTexture(4, new File(path + "mossy.png"));
            loadTexture(5, new File(path + "purplestone.png"));
            loadTexture(6, new File(path + "redbrick.png"));
            loadTexture(7, new File(path + "wood.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadTexture(int index, File file) throws IOException {
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixleColor = image.getRGB(x, y);
                textures[index][TEXTURE_WIDTH * x + y] = pixleColor;
            }
        }
    }

    private double deltaTime = 0.0;
    private double startTime = 0.0;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        System.out.println("Turning: " + turning);
        if (turning != 0) {
            player.turn(turning * Math.PI / 1.5 * deltaTime);
        }
        if (moving != 0) {
            player.move(moving * 2.5 * deltaTime);
        }

        deltaTime = (System.currentTimeMillis() - startTime) / 1000.0;
        startTime = System.currentTimeMillis();

        if (usernameField.isVisible()) {
            // center componenets
            usernameField.setBounds(getCenteredX(150), getCenteredY(30), 150, 30);
            enterGameButton.setBounds(getCenteredX(150), getCenteredY(30) + 40, 150, 30);

            // color background black
            g.setColor(Color.black);
            g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.PLAIN, 40));
            int titleWidth = g.getFontMetrics(g.getFont()).stringWidth("Spellcaster 3D");
            g.drawString("Spellcaster 3D", getCenteredX(titleWidth), 200);
            return;
        }

        renderWorld(g);
        // renderMinimap(g);

        g.setColor(Color.RED);
        g.drawString("FPS: " + ((int) (1.0 / deltaTime)), 10, 20);

    }

    public void renderWorld(Graphics g) {
        // cast a ray for every horizontal pixle on-screen
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            double cameraX = 2 * x / (double) SCREEN_WIDTH - 1;
            Vector3 dir = player.cameraPlane.mult(cameraX).add(player.dir);

            castRay(g, player.worldPos.x, player.worldPos.y, dir, x);
        }

        // draw every pixle on screen from the buffer
        for (int row = 0; row < pixleBuffer.length; row++) {
            for (int col = 0; col < pixleBuffer[0].length; col++) {
                g.setColor(new Color(pixleBuffer[row][col]));
                g.fillRect(col, row, 1, 1);
            }
        }

        // clear buffer
        pixleBuffer = new int[SCREEN_HEIGHT][SCREEN_WIDTH];

    }

    public void renderMinimap(Graphics g) {
        int cellSize = (int) (MINIMAP_SCALE * CELL_SIZE);
        int playerRadius = (int) (MINIMAP_SCALE * 5);

        // render cells
        for (int row = 0; row < worldMap[0].length; row++) {
            for (int col = 0; col < worldMap.length; col++) {
                // fill cell
                if (worldMap[row][col] > 0) {
                    g.setColor(Color.blue);
                    g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }

                // TEMPORARY check if player is in box
                if ((int) (player.worldPos.x) == col && (int) (player.worldPos.y) == row) {
                    g.setColor(Color.yellow);
                    g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }

                // outline cell
                g.setColor(Color.black);
                g.drawRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }

        // render player(s)
        int playerX = (int) (player.worldPos.x * cellSize);
        int playerY = (int) (player.worldPos.y * cellSize);

        g.setColor(Color.red);
        g.fillOval(playerX - playerRadius, playerY - playerRadius, playerRadius * 2, playerRadius * 2);

        // render camera
        int lineLength = (int) (10 * MINIMAP_SCALE);

        int endCameraX1 = playerX + (int) (player.dir.x * lineLength) + (int) (player.cameraPlane.x * lineLength);
        int endCameraY1 = playerY + (int) (player.dir.y * lineLength) + (int) (player.cameraPlane.y * lineLength);

        int endCameraX2 = playerX + (int) (player.dir.x * lineLength) - (int) (player.cameraPlane.x * lineLength);
        int endCameraY2 = playerY + (int) (player.dir.y * lineLength) - (int) (player.cameraPlane.y * lineLength);

        g.drawOval(endCameraX1 - 2, endCameraY1 - 2, 4, 4);
        g.drawOval(endCameraX2 - 2, endCameraY2 - 2, 4, 4);

    }

    /**
     * Cast ray from current player position given initial angle
     * 
     * Using Lodes's implementation of DDA algorithm
     * (https://lodev.org/cgtutor/raycasting.html)
     * 
     * @param rayAngle the angle of the ray in radians
     */
    public void castRay(Graphics g, double startX, double startY, Vector3 dir, int x) {

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
        double maxDist = worldMap.length; // max distance algorithm will check
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

            if (col >= 0 && col < worldMap[0].length && row >= 0 && row < worldMap.length) {
                if (worldMap[row][col] >= 1) {
                    hitDetected = true;

                }
            }
        }

        // guard against rendering squares that don't exist
        if (!hitDetected) {
            return;
        }

        // // visualize ray and collision
        // double worldHitX = rayOrigin.x + rayDir.x * worldDistToHit;
        // double worldHitY = rayOrigin.y + rayDir.y * worldDistToHit;

        // double screenHitX = worldHitX * CELL_SIZE * MINIMAP_SCALE;
        // double screenHitY = worldHitY * CELL_SIZE * MINIMAP_SCALE;

        // double screenOriginX = rayOrigin.x * CELL_SIZE * MINIMAP_SCALE;
        // double screenOriginY = rayOrigin.y * CELL_SIZE * MINIMAP_SCALE;

        // Line2D line = new Line2D.Double(screenOriginX, screenOriginY, screenHitX,
        // screenHitY);
        // Ellipse2D point = new Ellipse2D.Double(screenHitX - 1, screenHitY - 1, 2, 2);

        // Graphics2D g2 = (Graphics2D) g;
        // g2.setColor(Color.green);
        // g2.draw(line);
        // g2.fill(point);

        // fix fisheye effect
        double perpWallDist;
        if (side == HORIZONTAL_WALL) {
            perpWallDist = rayLength.x - unitStepSize.x;
        } else {
            perpWallDist = rayLength.y - unitStepSize.y;
        }

        int lineHeight = (int) (SCREEN_HEIGHT / perpWallDist);

        int drawStart = (SCREEN_HEIGHT - lineHeight) / 2;
        if (drawStart < 0) {
            drawStart = 0;
        }

        int drawEnd = (SCREEN_HEIGHT + lineHeight) / 2;
        if (drawEnd >= SCREEN_HEIGHT) {
            drawEnd = SCREEN_HEIGHT - 1;
        }

        int textureNum = worldMap[(int) mapCheck.y][(int) mapCheck.x] - 1;

        double wallOffset;
        if (side == HORIZONTAL_WALL) {
            wallOffset = rayOrigin.y + perpWallDist * rayDir.y;
        } else {
            wallOffset = rayOrigin.x + perpWallDist * rayDir.x;
        }
        wallOffset -= (int) wallOffset;

        // get x position on texture
        int textureX = (int) (wallOffset * (double) TEXTURE_WIDTH);
        if (side == 0 && rayDir.x > 0)
            textureX = TEXTURE_WIDTH - textureX - 1;
        if (side == 1 && rayDir.y < 0)
            textureX = TEXTURE_WIDTH - textureX - 1;

        // loop through y values downward
        double vertStep = 1.0 * TEXTURE_HEIGHT / lineHeight;
        double texturePos = (drawStart - SCREEN_HEIGHT / 2 + lineHeight / 2) * vertStep;
        for (int y = drawStart; y < drawEnd; y++) {
            int textureY = (int) texturePos & (TEXTURE_HEIGHT - 1);
            texturePos += vertStep;
            int color = textures[textureNum][TEXTURE_WIDTH * textureX + textureY];
            if (side == VERTICAL_WALL) {
                color = (color >> 1) & 8355711; // shading magic
            }
            pixleBuffer[y][x] = color;
        }

    }

    /**
     * Do the listening stuff
     */
    public void poll() {
        while (true) {
            try {
                Message message = (Message) in.readObject();

                switch (message.tag) {
                    default:
                        break;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();

        if (source == enterGameButton) {
            try {
                out.writeObject(new Message(Message.Tag.READY_TO_PLAY));
            } catch (IOException e) {
                e.printStackTrace();
            }
            username = usernameField.getText();
            myFrame.setTitle(username);
            usernameField.setVisible(false);
            enterGameButton.setVisible(false);

        }

        repaint();
    }

    private int turning = 0;
    private int moving = 0;

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_D) {
            // player.turn(-Math.PI / 1.1 * deltaTime);
            turning = -1;

        } else if (keyCode == KeyEvent.VK_A) {
            // player.turn(Math.PI / 1.1 * deltaTime);
            turning = 1;

        } else if (keyCode == KeyEvent.VK_W) {
            moving = 1;
            // player.move(4 * deltaTime);

        } else if (keyCode == KeyEvent.VK_S) {
            moving = -1;
            // player.move(-4 * deltaTime);

        } else if (keyCode == KeyEvent.VK_UP) {
            player.dir = player.dir.mult(1.1);

        } else if (keyCode == KeyEvent.VK_DOWN) {
            player.dir = player.dir.mult(0.9);

        } else if (keyCode == KeyEvent.VK_LEFT) {
            player.cameraPlane = player.cameraPlane.mult(1.1);

        } else if (keyCode == KeyEvent.VK_RIGHT) {
            player.cameraPlane = player.cameraPlane.mult(0.9);

        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_A) {
            turning = 0;
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S) {
            moving = 0;
        }

        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public int getCenteredX(int componentWidth) {
        return (SCREEN_WIDTH - componentWidth) / 2;
    }

    public int getCenteredY(int componentHeight) {
        return (SCREEN_HEIGHT - componentHeight) / 2;
    }

    public void animate() {
        while (true) {
            repaint();
        }
    }
}
