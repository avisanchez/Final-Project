import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.util.UUID;

import core.*;
import core.Renderer;
import core.gameobject.Player;
import core.gameobject.Sound;
import core.mydatastruct.*;

public class Screen extends JPanel implements ActionListener, MouseMotionListener, MouseListener, KeyListener {

    // io streams
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // game handlers
    private Renderer renderer;
    private ScreenAnimator screenAnimator;

    // game objects
    private Player player;
    private MyArrayList<Player> otherPlayers;

    // jcomponents
    private JTextField usernameField;
    private JButton enterGameButton;

    private String username;

    private Robot robot;
    private JFrame frame;

    private BufferedImage wand, hotbar, crosshair, backdrop, lightning;

    int mouseX;
    int mouseDelta;
    int lastX;

    public Screen(Socket server, JFrame frame) {
        otherPlayers = new MyArrayList<>();
        lastX = 400;

        Sound.backgroundMusic.play();

        this.frame = frame;

        // Connect to server
        try {
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());

        } catch (IOException e) {
            System.out.println("ERROR: Failed to get server input and output streams.");
            e.printStackTrace();

        }

        try {
            wand = ImageIO.read(new File("core/resource/sprite/wand.png"));
            hotbar = ImageIO.read(new File("core/resource/sprite/hotbar.png"));
            crosshair = ImageIO.read(new File("core/resource/sprite/crosshair.png"));
            backdrop = ImageIO.read(new File("core/resource/texture/backdrop1.jpeg"));
            lightning = ImageIO.read(new File("core/resource/sprite/lightning.png"));
        } catch (IOException e) {
            System.out.println(e);
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
        addMouseListener(this);
        addMouseMotionListener(this);

        // start animation loop
        screenAnimator = new ScreenAnimator(this);
        Thread animationThread = new Thread(screenAnimator);
        animationThread.start();

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (username == null) {
            drawTitleScreen(g);
            return;
        }

        if (player.inMotion()) {
            player.update(screenAnimator.getDeltaTime());

            // send out updated player info
            try {
                String updatedPlayer = player.id.toString() + "," + player.worldPos.x + "," + player.worldPos.y;

                out.reset();
                out.writeObject(new Message(Message.Tag.UPDATE_PLAYER, updatedPlayer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        g.drawImage(backdrop, 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT / 2, 0, 0,
                Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT / 2, null);

        // render game
        if (renderer != null) {
            renderer.render(g, otherPlayers);
        }

        //

        // draw fps
        g.setColor(Color.RED);
        g.drawString("FPS: " + screenAnimator.getFPS(), 10, 20);
        g.drawString("Avg FPS: " + screenAnimator.getAvgFPS(), 10, 40);

        g.drawImage(wand, 500, 300, 220, 300, null);
        g.drawImage(hotbar, 0, 500, 800, 100, null);
        g.drawImage(crosshair, (Settings.SCREEN_WIDTH - 25) / 2,
                (Settings.SCREEN_HEIGHT - 25) / 2, 25, 25, null);

        // MapManager.drawMinimap(g, player, otherPlayers);

        if (showImage) {
            Graphics2D g2d = (Graphics2D) g;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(ac);
            g2d.drawImage(lightning, 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, null);
        }

    }

    private boolean showImage = false;
    private float alpha = 0.5f;

    private void fadeOut(Graphics g) {
        showImage = true;

        Runnable animator = new Runnable() {

            @Override
            public void run() {
                while (alpha > 0.0f) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    alpha -= 0.01f;

                }
                alpha = 0.5f;
                showImage = false;
            }

        };
        Thread animationThread = new Thread(animator);
        animationThread.start();
    }

    // -----------------------------------
    // -------- Network Listener ---------
    // -----------------------------------
    public void poll() {
        try {
            while (true) {

                Message message = (Message) in.readObject();
                String[] data = ((String) message.getData()).split(",");

                switch (message.tag) {
                    case CREATE_PLAYER:
                        UUID idToCreate = UUID.fromString(data[0]);
                        double worldX = Double.parseDouble(data[1]);
                        double worldY = Double.parseDouble(data[2]);
                        int textureNum = Integer.parseInt(data[3]);

                        Player newPlayer = new Player(idToCreate, worldX, worldY, textureNum);
                        otherPlayers.add(newPlayer.copy());
                        break;
                    case ASSIGN_PLAYER:
                        UUID idToAssign = UUID.fromString(data[0]);

                        Player tempPlayer = new Player(idToAssign, 0, 0, 0);

                        player = otherPlayers.remove(tempPlayer).copy();
                        renderer = new Renderer(player, frame);

                        break;
                    case UPDATE_PLAYER:
                        UUID idToUpdate = UUID.fromString(data[0]);
                        double updatedX = Double.parseDouble(data[1]);
                        double updatedY = Double.parseDouble(data[2]);

                        Player updatedPlayer = new Player(idToUpdate, updatedX, updatedY, 8);

                        if (updatedPlayer.equals(this.player)) {
                            // this.player.worldPos = updatedPlayer.worldPos.copy();
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
                    case DELETE_PLAYER:

                        UUID idToDelete = UUID.fromString(data[0]);
                        Player playerToDelete = new Player(idToDelete, 0, 0, 0);

                        otherPlayers.remove(playerToDelete);
                        System.out.println("other players size: " + otherPlayers.size());
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

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A)
            player.setTurnDir(1);
        else if (keyCode == KeyEvent.VK_D)
            player.setTurnDir(-1);
        else if (keyCode == KeyEvent.VK_W)
            player.setMoveDir(1);
        else if (keyCode == KeyEvent.VK_S)
            player.setMoveDir(-1);
        else if (keyCode == KeyEvent.VK_SPACE) {
            System.out.println("SHOT FIRED!");
            fadeOut(getGraphics());
            // for (Player otherPlayer : otherPlayers) {
            // boolean enemyHit = renderer.castRay(player.worldPos.x, player.worldPos.y,
            // otherPlayer);

            // if (enemyHit) {
            // System.out.println("COLLISION DETECTED");
            // try {
            // // id,x,y
            // String messageString = player.id.toString() + "," + 1.5 + "," + 1.5;
            // out.reset();
            // out.writeObject(
            // new Message(Message.Tag.UPDATE_PLAYER, messageString));
            // } catch (IOException e1) {
            // e1.printStackTrace();
            // }
            // }

            // }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_A)
            player.setTurnDir(0);
        else if (keyCode == KeyEvent.VK_D)
            player.setTurnDir(0);
        else if (keyCode == KeyEvent.VK_W)
            player.setMoveDir(0);
        else if (keyCode == KeyEvent.VK_S)
            player.setMoveDir(0);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        System.out.println("mouse entered");
        // robot.mouseMove(400, 300);
    }

    public void mouseExited(MouseEvent e) {
        System.out.println("mouse exited");

    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        // mouseX = (int) (e.getX());
        // System.out.println("MouseX: " + mouseX);
        // mouseDelta = Math.abs(lastX - mouseX);
        // System.out.println("MouseDelta: " + mouseDelta);
        // lastX = mouseX;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

}
