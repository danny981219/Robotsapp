package bg.tu.robot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ISThread extends Thread {

    private int port;

    private int robot;

    public ISThread(int port, int robot) {
        this.port = port;
        this.robot = robot;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server: listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Server: new client connected on port " + port);
                new ISRobotThread(socket, robot).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception (" + port + "): " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
