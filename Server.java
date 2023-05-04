import java.net.*;
import java.io.*;

public class Server {
    public static final int PORT = 1024;
    private static Manager manager = new Manager();

    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
        ServerSocket server = new ServerSocket(PORT);

        while (true) {
            System.out.println("Waiting for a connection...");
            Socket client = server.accept();

            System.out.println("Connection made.");
            manager.addClient(client);
        }
    }
}
