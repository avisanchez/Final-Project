import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import core.MapManager;
import core.ObjectManager;
import core.Renderer;
import core.gameobjects.Player;
import core.mydatastructs.Message;
import core.Settings;

public class Screen extends JPanel implements ActionListener, KeyListener {

    // io streams
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // jcomponents
    private JTextField usernameField;
    private JButton enterGameButton;

    // game objects
    private ObjectManager objectManager;
    private Renderer renderer;
    private Player player;
    private int maxFPS, turning, moving;
    private double deltaTime, startTime;

    private String username;

    public Screen(Socket server) {
        ObjectManager objectManager = new ObjectManager();
        player = new Player(1.5, 1.5);
        renderer = new Renderer(player, objectManager);
        maxFPS = 0;
        turning = moving = 0;
        deltaTime = startTime = 0;

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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // update delta time
        deltaTime = (System.currentTimeMillis() - startTime) / 1000.0;
        startTime = System.currentTimeMillis();

        if (username == null) {
            drawTitleScreen(g);
            return;
        }

        // move player
        if (turning != 0) {
            player.turn(turning * Math.PI / 1.5 * deltaTime);
        }
        if (moving != 0) {
            player.move(moving * 2.5 * deltaTime);
        }

        renderer.update();
        renderer.render(g);

        // update fps
        int fps = (int) (1.0 / deltaTime);

        // draw fps
        g.setColor(Color.RED);
        g.drawString("FPS: " + fps, 10, 20);

        // calculate max fps
        if (fps > maxFPS) {
            maxFPS = fps;
        }

    }

    public void animate() {
        while (true) {
            // run at around 60 fps
            try {
                Thread.sleep((long) 16.66);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            repaint();

        }
    }

    // -----------------------------------
    // -------- Network Listener ---------
    // -----------------------------------
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

    // -----------------------------------
    // --------- Action Listener ---------
    // -----------------------------------

    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();

        if (source == enterGameButton) {
            try {
                out.writeObject(new Message(Message.Tag.READY_TO_PLAY));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        } else if (keyCode == KeyEvent.VK_UP) {
            player.dir = player.dir.mult(1.1);

        } else if (keyCode == KeyEvent.VK_DOWN) {
            player.dir = player.dir.mult(0.9);

        } else if (keyCode == KeyEvent.VK_LEFT) {
            player.cameraPlane = player.cameraPlane.mult(1.1);

        } else if (keyCode == KeyEvent.VK_RIGHT) {
            player.cameraPlane = player.cameraPlane.mult(0.9);

        } else if (keyCode == KeyEvent.VK_SPACE) {
            System.out.println("Max FPS: " + maxFPS);
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

}
