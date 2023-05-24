import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.UUID;

import core.*;
import core.Renderer;
import core.gameobject.Player;
import core.mydatastruct.*;

public class Screen extends JPanel implements ActionListener {

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

    public Screen(Socket server) {
        otherPlayers = new MyArrayList<>();

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

        // start animation loop
        screenAnimator = new ScreenAnimator(this);
        Thread animationThread = new Thread(screenAnimator);
        animationThread.start();

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
                out.reset();
                out.writeObject(new Message(Message.Tag.UPDATE_PLAYER, player));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // render game
        if (renderer != null) {
            renderer.render(g, otherPlayers);
        }

        //
        MapManager.drawMinimap(g, player, otherPlayers);

        // draw fps
        g.setColor(Color.RED);
        g.drawString("FPS: " + screenAnimator.getFPS(), 10, 20);
        g.drawString("Avg FPS: " + screenAnimator.getAvgFPS(), 10, 40);

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
                        UUID idToAssign = (UUID) message.getData();

                        Player tempPlayer = new Player(idToAssign, 0, 0, 0);
                        player = otherPlayers.remove(tempPlayer).copy();
                        renderer = new Renderer(player);

                        addKeyListener(player);
                        addMouseListener(player);
                        addMouseMotionListener(player);
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
                    case DELETE_PLAYER:
                        UUID idToDelete = (UUID) message.getData();
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

}
