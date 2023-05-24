import javax.swing.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String hostName = "localhost"; // ip address
        int port = Server.PORT;
        try {
            Socket server = new Socket(hostName, port);

            JFrame fr = new JFrame("Client");
            Screen sc = new Screen(server);
            fr.add(sc);
            fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fr.pack();
            fr.setVisible(true);
            fr.setResizable(false);

            // Create a new blank cursor.
            // BufferedImage cursorImg = new BufferedImage(16, 16,
            // BufferedImage.TYPE_INT_ARGB);
            // Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            // cursorImg, new Point(0, 0), "blank cursor");
            // fr.getContentPane().setCursor(blankCursor);

            sc.setBackground(Color.black);

            sc.poll();

            // close this window without affecting other clients
            fr.dispatchEvent(new WindowEvent(fr, WindowEvent.WINDOW_CLOSING));
            server.close();
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Invalid host name.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Failed to connect to server.");
            e.printStackTrace();
        }

    }
}
