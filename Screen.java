import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Screen extends JPanel implements ActionListener {
    private Dimension dim;
    private JFrame myFrame;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JTextField usernameField;
    private JButton enterGameButton;

    private String username;

    public Screen(Socket server, JFrame frame) {
        dim = new Dimension(800, 600);
        this.myFrame = frame;

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
        usernameField.setBounds(getCenteredX(150), getCenteredY(30), 150, 30);
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameField);

        enterGameButton = new JButton("Ready to Play");
        enterGameButton.setBounds(getCenteredX(150), getCenteredY(30) + 40, 150, 30);
        enterGameButton.addActionListener(this);
        enterGameButton.setHorizontalAlignment(SwingConstants.CENTER);
        add(enterGameButton);

        setLayout(null);
        setFocusable(true);
    }

    public int getCenteredX(int componentWidth) {
        return (int) (dim.getWidth() - componentWidth) / 2;
    }

    public int getCenteredY(int componentHeight) {
        return (int) (dim.getHeight() - componentHeight) / 2;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();

        if (source == enterGameButton) {
            try {
                out.writeObject(new Message(Message.Tag.READY_TO_PLAY));
            } catch (IOException e) {
                e.printStackTrace();
            }
            myFrame.setTitle(usernameField.getText());
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
        return dim;
    }
}
