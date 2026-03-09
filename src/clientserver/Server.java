package clientserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static List<ClientConnection> clients = new ArrayList<>();

    public static void main(String[] args) {

        int port = 1234;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {

                Socket socket = serverSocket.accept();
                ClientConnection client = new ClientConnection(socket, clients);
                clients.add(client);
                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}