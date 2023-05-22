import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;
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

            sc.setBackground(Color.black);

            ScreenAnimator screenAnimator = new ScreenAnimator(sc);
            Thread animationThread = new Thread(screenAnimator);
            animationThread.start();

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

class ScreenAnimator implements Runnable {
    Screen target;

    public ScreenAnimator(Screen target) {
        this.target = target;
    }

    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                break;
            }

            // run game at ~ 60 fps
            try {
                Thread.sleep((long) 16.66);

            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            target.repaint();

        }
    }

}
