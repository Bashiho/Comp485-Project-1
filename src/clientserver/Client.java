package clientserver;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 1234;

        try {
            Socket socket = new Socket(host, port);

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            //Input username/password, automatically checked by ClientConnection
            //If no account exists, creates one
            //If wrong password, allows user to try again but currently doesn't prompt them to, requires fix
            System.out.print("Enter username: ");
            String username = keyboard.readLine();
            System.out.print("Enter Password: ");
            String password = keyboard.readLine();

            // listen/read messages to and from server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            
            String message;
            while ((message = keyboard.readLine()) != null) {
                out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}