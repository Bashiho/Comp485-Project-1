package clientserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ClientConnection implements Runnable {

   private Socket socket;
   private List<ClientConnection> clientList;
   private BufferedReader input;
   private PrintWriter output;
   private String usernameInput;
   private String passwordInput;
   private boolean created;
   private String verifyOutput;

   public ClientConnection(Socket socket, List<ClientConnection> clientList) {
      this.socket = socket;
      this.clientList = clientList;
   }

   public void run() {
   
      try {
      
         input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         output = new PrintWriter(socket.getOutputStream(), true);
            // first message from client is the username
         while(created == false){
            //System.out.println("Enter Username: ");
            usernameInput = input.readLine();
            //System.out.println("Enter Password: ");
            passwordInput = input.readLine();
            System.out.println(verifySignin(usernameInput, passwordInput));
         }
         String message;
      
         while ((message = input.readLine()) != null) {
            broadcast(usernameInput + ": " + message);
         }
      
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void broadcast(String message) {
   
      for (ClientConnection client : clientList) {
         if (client.output != null) {
            client.output.println(message);
         }
      }
   
   }
    
   public String verifySignin(String username, String password) {
      String filepath = "data.txt";
      String delimiter = ",";
      String currentLine;
      String[] data;
      
      try {
         FileReader fr = new FileReader(filepath);
         BufferedReader br = new BufferedReader(fr);
      
         while((currentLine = br.readLine()) != null){
            data = currentLine.split(delimiter);
            if(data[0].equals(username) && data[1].equals(password)){
               created = true;
               String returnString = username + " joined the chat";
               return returnString;
            } 
            else if(data[0].equals(username) && !data[1].equals(password))
               return "Incorrect Password";
         }
         //If account not found, create one 
         created = write(username, password);
         if(created)
            return "Account Created";
           
      }
      catch(Exception e){
         System.out.print(e);
      }
      return "Failed to get Account";
   }    
            
   private boolean write(String username, String password) {
      Path currentPath = Paths.get("").toAbsolutePath();
      Path data = currentPath.resolve("data.txt");
      String user = username + ", " + password + "\n";
      try{
         Files.write(data, user.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         return true;
      }
      catch(IOException e){
         e.printStackTrace();
      }
      return false;
   }
}