import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.UUID;

import core.*;
import core.Renderer;
import core.gameobjects.Player;
import core.mydatastructs.*;

public class Screen extends JPanel implements ActionListener, KeyListener {

    // io streams
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // jcomponents
    private JTextField usernameField;
    private JButton enterGameButton;

    // game objects
    private Renderer renderer;

    private Player player;
    private MyArrayList<Player> otherPlayers;

    private int maxFPS, turning, moving;
    private double deltaTime, startTime;
    private Pair<Double, Integer> avgFPS = new Pair<Double, Integer>(0.0, 0);

    private String username;

    public Screen(Socket server) {
        maxFPS = 0;
        turning = moving = 0;
        startTime = deltaTime = 0;

        otherPlayers = new MyArrayList<>();

        username = null;

        // Connect to server
        try {
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());

        } catch (IOException e) {
            System.out.println("ERROR: Failed to get server input and output streams.");
            e.printStackTrace();

        }

        // Set up jcomponents
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

    public boolean readyToRepaint = true;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        readyToRepaint = false;

        if (username == null) {
            drawTitleScreen(g);
            return;
        }

        // update delta time
        deltaTime = (System.currentTimeMillis() - startTime) / 1000.0;
        startTime = System.currentTimeMillis();

        // update fps
        int fps = (int) (1.0 / deltaTime);

        // calculate max fps
        if (fps > maxFPS) {
            maxFPS = fps;
        }

        // move player
        if (turning != 0) {
            player.turn(turning * Math.PI / 1.5 * deltaTime);

        }
        if (moving != 0) {
            player.move(moving * 2.5 * deltaTime);

        }

        // update player coordinates in object manager
        if (turning != 0 || moving != 0) {
            try {
                out.reset();
                out.writeObject(new Message(Message.Tag.UPDATE_PLAYER, player));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        drawBackground(g);

        // render game
        if (renderer != null) {
            renderer.render(g, otherPlayers);
        }
        MapManager.drawMinimap(g, player, otherPlayers);

        // draw fps
        g.setColor(Color.RED);
        g.drawString("FPS: " + fps, 10, 20);

        avgFPS._0 += fps;
        avgFPS._1++;

    }

    // -----------------------------------
    // -------- Network Listener ---------
    // -----------------------------------
    public void poll() {
        try {
            while (true) {

                Message message = (Message) in.readObject();

                switch (message.tag) {
                    case CREATE_PLAYER:
                        Player newPlayer = (Player) message.getData();

                        otherPlayers.add(newPlayer.copy());
                        break;
                    case ASSIGN_PLAYER:
                        UUID id = (UUID) message.getData();

                        Player tempPlayer = new Player(id, 0, 0, 0);
                        player = otherPlayers.remove(tempPlayer).copy();
                        renderer = new Renderer(player);
                        break;
                    case UPDATE_PLAYER:
                        Player updatedPlayer = (Player) message.getData();
                        if (updatedPlayer.equals(this.player)) {
                            break;
                        }

                        boolean playerExists = false;
                        for (int i = 0; i < otherPlayers.size(); i++) {

                            if (otherPlayers.get(i).equals(updatedPlayer)) {
                                otherPlayers.set(i, updatedPlayer.copy());
                                playerExists = true;
                                break;
                            }
                        }
                        if (!playerExists) {
                            otherPlayers.add(updatedPlayer.copy());

                        }

                        break;
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // -----------------------------------
    // --------- Action Listener ---------
    // -----------------------------------

    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();

        if (source == enterGameButton) {
            username = usernameField.getText();
            usernameField.setVisible(false);
            enterGameButton.setVisible(false);

        }

        repaint();
    }

    // -----------------------------------
    // ----------- Key Listener ----------
    // -----------------------------------

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_D) {
            turning = -1;

        } else if (keyCode == KeyEvent.VK_A) {
            turning = 1;

        } else if (keyCode == KeyEvent.VK_W) {
            moving = 1;

        } else if (keyCode == KeyEvent.VK_S) {
            moving = -1;
        }

        // } else if (keyCode == KeyEvent.VK_UP) {
        // player.dir = player.dir.mult(1.1);

        // } else if (keyCode == KeyEvent.VK_DOWN) {
        // player.dir = player.dir.mult(0.9);

        // } else if (keyCode == KeyEvent.VK_LEFT) {
        // player.cameraPlane = player.cameraPlane.mult(1.1);

        // } else if (keyCode == KeyEvent.VK_RIGHT) {
        // player.cameraPlane = player.cameraPlane.mult(0.9);

        // }
        else if (keyCode == KeyEvent.VK_SPACE) {
            System.out.println("Max FPS: " + maxFPS);
            System.out.println("Avg FPS: " + (avgFPS._0 / (1.0 * avgFPS._1)));

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

    // -----------------------------------
    // ---------- Other Methods ----------
    // -----------------------------------

    public Dimension getPreferredSize() {
        return new Dimension(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
    }

    private int getCenteredX(int componentWidth) {
        return (Settings.SCREEN_WIDTH - componentWidth) / 2;
    }

    private int getCenteredY(int componentHeight) {
        return (Settings.SCREEN_HEIGHT - componentHeight) / 2;
    }

    public void drawTitleScreen(Graphics g) {
        // center componenets
        usernameField.setBounds(getCenteredX(150), getCenteredY(30), 150, 30);
        enterGameButton.setBounds(getCenteredX(150), getCenteredY(30) + 40, 150, 30);

        // draw title
        g.setColor(Color.red);
        g.setFont(new Font("serif", Font.PLAIN, 40));
        int titleWidth = g.getFontMetrics(g.getFont()).stringWidth("Spellcaster 3D");
        g.drawString("Spellcaster 3D", getCenteredX(titleWidth), 200);
    }

    public void drawBackground(Graphics g) {
        g.setColor(new Color(56, 56, 56));
        g.fillRect(0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT / 2);

        g.setColor(new Color(112, 112, 112));
        g.fillRect(0, Settings.SCREEN_HEIGHT / 2, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT / 2);
    }

}
