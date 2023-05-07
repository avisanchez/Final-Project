import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import mydatastructs.*;

public class Screen extends JPanel implements ActionListener, KeyListener {
    private JFrame myFrame;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JTextField usernameField;
    private JButton enterGameButton;

    private String username;

    public final int[][] map = new int[][] {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 0, 0, 0, 0, 5, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 0, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
    };

    private final int CELL_SIZE = 32;
    private final double MINIMAP_SCALE = 0.75;

    private Player player;

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

    }

    public int getCenteredX(int componentWidth) {
        return (this.getWidth() - componentWidth) / 2;
    }

    public int getCenteredY(int componentHeight) {
        return (this.getHeight() - componentHeight) / 2;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (usernameField.isVisible()) {
            // center componenets
            usernameField.setBounds(getCenteredX(150), getCenteredY(30), 150, 30);
            enterGameButton.setBounds(getCenteredX(150), getCenteredY(30) + 40, 150, 30);

            // color background black
            g.setColor(Color.black);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.PLAIN, 30));
            g.drawString("Spellcaster 3D", 100, 100);
            return;
        }

        renderWorld(g);
        // renderMinimap(g);

    }

    public void renderWorld(Graphics g) {
        for (int x = 0; x <= getWidth(); x++) {
            double cameraX = 2 * x / (double) getWidth() - 1;
            Vector3 dir = player.cameraPlane.mult(cameraX).add(player.dir);

            castRay(g, player.worldPos.x, player.worldPos.y, dir, x);
        }
    }

    public void renderMinimap(Graphics g) {
        int cellSize = (int) (MINIMAP_SCALE * CELL_SIZE);
        int playerRadius = (int) (MINIMAP_SCALE * 5);

        // render cells
        for (int row = 0; row < map[0].length; row++) {
            for (int col = 0; col < map.length; col++) {
                // fill cell
                if (map[row][col] > 0) {
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
        boolean hitDetected = false;
        double maxWorldDist = map.length; // max distance algorithm will check
        double worldDist = 0;
        int side = -1;

        while (!hitDetected && worldDist < maxWorldDist) {

            if (rayLength.x < rayLength.y) {
                mapCheck.x += step.x;
                worldDist = rayLength.x;
                rayLength.x += unitStepSize.x;
                side = 0;

            } else {
                mapCheck.y += step.y;
                worldDist = rayLength.y;
                rayLength.y += unitStepSize.y;
                side = 1;

            }

            int row = (int) mapCheck.y;
            int col = (int) mapCheck.x;

            if (col >= 0 && col < map[0].length && row >= 0 && row < map.length) {
                if (map[row][col] >= 1) {
                    hitDetected = true;

                }
            }
        }

        if (hitDetected) {
            // visualize ray and collision
            // double worldHitX = rayOrigin.x + rayDir.x * worldDist;
            // double worldHitY = rayOrigin.y + rayDir.y * worldDist;

            // double screenHitX = worldHitX * CELL_SIZE * MINIMAP_SCALE;
            // double screenHitY = worldHitY * CELL_SIZE * MINIMAP_SCALE;

            // double screenOriginX = rayOrigin.x * CELL_SIZE * MINIMAP_SCALE;
            // double screenOriginY = rayOrigin.y * CELL_SIZE * MINIMAP_SCALE;

            // Line2D line = new Line2D.Double(screenOriginX, screenOriginY, screenHitX,
            // screenHitY);
            // Ellipse2D point = new Ellipse2D.Double(screenHitX - 1, screenHitY - 1, 2, 2);

            // Graphics2D g2 = (Graphics2D) g;
            // g2.draw(line);
            // g2.fill(point);

            double perpWallDist;
            if (side == 0) {
                perpWallDist = rayLength.x - unitStepSize.x;
            } else {
                perpWallDist = rayLength.y - unitStepSize.y;
            }

            int lineHeight = (int) (getHeight() / (perpWallDist));
            int drawStart = -lineHeight / 2 + getHeight() / 2;
            if (drawStart < 0) {
                drawStart = 0;
            }
            int drawEnd = lineHeight / 2 + getHeight() / 2;
            if (drawEnd >= getHeight()) {
                drawEnd = getHeight() - 1;
            }

            switch (map[(int) mapCheck.y][(int) mapCheck.x]) {
                case 1:
                    g.setColor(Color.red);
                    ;
                    break; // red
                case 2:
                    g.setColor(Color.green);
                    break; // green
                case 3:
                    g.setColor(Color.blue);
                    break; // blue
                case 4:
                    g.setColor(Color.white);
                    break; // white
                default:
                    g.setColor(Color.yellow);
                    break; // yellow
            }

            if (side == 1) {
                g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 150));
            }

            g.drawLine(x, drawStart, x, drawEnd);

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

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_D) {
            player.turn(-Math.PI / 24);

        } else if (keyCode == KeyEvent.VK_A) {
            player.turn(Math.PI / 24);

        } else if (keyCode == KeyEvent.VK_W) {
            player.move(0.5);

        } else if (keyCode == KeyEvent.VK_S) {
            player.move(-0.5);

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

    }
}
