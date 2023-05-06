import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

import mydatastructs.*;

public class Screen extends JPanel implements ActionListener, KeyListener {
    private JFrame myFrame;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JTextField usernameField;
    private JButton enterGameButton;

    private String username;

    public final int[][] map = new int[][] {
            { 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 0, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 1 },
            { 1, 0, 1, 0, 1, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1 }
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

            g.setColor(Color.black);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.PLAIN, 30));
            g.drawString("Spellcaster 3D", 100, 100);
            return;
        }

        renderMinimap(g);

        DecimalFormat d = new DecimalFormat("#.##");
        g.drawString("Player angle rads: " + d.format(player.angle), getWidth() - 300, 20);
        g.drawString("Player angle degs: " + d.format(Math.toDegrees(player.angle)), getWidth() - 300, 40);

        for (int i = -5; i <= 5; i++) {
            castRay(g, player.worldPos.x, player.worldPos.y, player.angle + i * Math.PI / 50);
        }

    }

    public void renderMinimap(Graphics g) {
        int cellSize = (int) (MINIMAP_SCALE * CELL_SIZE);
        int playerRadius = (int) (MINIMAP_SCALE * player.radius2D);

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

    }

    /**
     * Cast ray from current player position given initial angle
     * 
     * Using Lodes's implementation of DDA algorithm
     * (https://lodev.org/cgtutor/raycasting.html)
     * 
     * @param rayAngle the angle of the ray in radians
     */
    public void castRay(Graphics g, double x, double y, double rayAngle) {

        // world coordinates of ray starting point
        Vector3 rayOrigin = new Vector3(x, y, 0);

        // normalized ray direction based on player angle
        Vector3 rayDir = new Vector3(Math.cos(rayAngle), -Math.sin(rayAngle), 0);

        // ray unit step size
        Vector3 unitStepSize = new Vector3(Math.abs(1.0f / rayDir.x), Math.abs(1.0f / rayDir.y), 0);

        // x and y direction algorithm will step in
        Vector3 stepDir = new Vector3();

        // current length of ray
        Vector3 rayLength = new Vector3();

        // integer map coordinates
        Vector3 mapCheck = new Vector3((int) rayOrigin.x, (int) rayOrigin.y, 0);

        // starting conditions
        if (rayDir.x > 0) {
            stepDir.x = 1;
            rayLength.x = ((mapCheck.x + 1) - rayOrigin.x) * unitStepSize.x;

        } else {
            stepDir.x = -1;
            rayLength.x = (rayOrigin.x - mapCheck.x) * unitStepSize.x;

        }

        if (rayDir.y > 0) {
            stepDir.y = 1;
            rayLength.y = ((mapCheck.y + 1) - rayOrigin.y) * unitStepSize.y;

        } else {
            stepDir.y = -1;
            rayLength.y = (rayOrigin.y - mapCheck.y) * unitStepSize.y;

        }

        // run algorithm

        boolean collisionDetected = false;
        double maxWorldDistance = 10; // max distance algorithm will check
        double worldDistance = 0;

        while (!collisionDetected && worldDistance < maxWorldDistance) {

            if (rayLength.x < rayLength.y) {
                mapCheck.x += stepDir.x;
                worldDistance = rayLength.x;
                rayLength.x += unitStepSize.x;

            } else {
                mapCheck.y += stepDir.y;
                worldDistance = rayLength.y;
                rayLength.y += unitStepSize.y;

            }

            int row = (int) mapCheck.y;
            int col = (int) mapCheck.x;

            if (col >= 0 && col < map[0].length && row >= 0 && row < map.length) {
                if (map[row][col] == 1) {
                    collisionDetected = true;

                }
            }
        }

        if (collisionDetected) {
            // visualize ray and collision
            double worldIntersectionX = player.worldPos.x + rayDir.x * worldDistance;
            double worldIntersectionY = player.worldPos.y + rayDir.y * worldDistance;

            double screenIntersectionX = worldIntersectionX * CELL_SIZE * MINIMAP_SCALE;
            double screenIntersectionY = worldIntersectionY * CELL_SIZE * MINIMAP_SCALE;

            double screenOriginX = rayOrigin.x * CELL_SIZE * MINIMAP_SCALE;
            double screenOriginY = rayOrigin.y * CELL_SIZE * MINIMAP_SCALE;

            Line2D connectingLine = new Line2D.Double(screenOriginX, screenOriginY, screenIntersectionX,
                    screenIntersectionY);
            Ellipse2D intersectionPoint = new Ellipse2D.Double(screenIntersectionX - 1, screenIntersectionY - 1, 2, 2);

            Graphics2D g2 = (Graphics2D) g;
            g2.draw(connectingLine);
            g2.fill(intersectionPoint);

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
        return new Dimension(800, 800);
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_D) {
            player.turn(-Math.PI / 24);

        } else if (keyCode == KeyEvent.VK_A) {
            player.turn(Math.PI / 24);

        } else if (keyCode == KeyEvent.VK_W) {
            player.move(1.0 / map.length);

        } else if (keyCode == KeyEvent.VK_S) {
            player.move(-1.0 / map.length);

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
