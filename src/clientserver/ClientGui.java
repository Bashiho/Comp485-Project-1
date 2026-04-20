package clientserver;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.*;

import java.util.ArrayList;
import java.util.Arrays;


public class ClientGui extends Thread{

  final JTextPane jtextFilDiscu = new JTextPane();
  final JTextPane jtextListUsers = new JTextPane();
  final JTextField jtextInputChat = new JTextField();
  private String oldMsg = "";
  private Thread read;
  private String serverName;
  private int PORT;
  private String name;
  private String password;
  BufferedReader input;
  PrintWriter output;
  Socket server;

  public ClientGui() {
    this.serverName = "localhost";
    this.PORT = 1234;
    this.name = "Username";
    this.password = "Password";

    String fontfamily = "Arial, sans-serif";
    Font font = new Font(fontfamily, Font.PLAIN, 15);

    // Box for chat
    final JFrame jfr = new JFrame("Chat");
    jfr.getContentPane().setLayout(null);
    jfr.setSize(700, 500);
    jfr.setResizable(false);
    jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    jtextFilDiscu.setBounds(25, 25, 490, 320);
    jtextFilDiscu.setFont(font);
    jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
    jtextFilDiscu.setEditable(false);
    JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
    jtextFilDiscuSP.setBounds(25, 25, 490, 320);

    jtextFilDiscu.setContentType("text/html");
    jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

    jtextListUsers.setBounds(525, 25, 156, 320);
    jtextListUsers.setEditable(true);
    jtextListUsers.setFont(font);
    jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
    jtextListUsers.setEditable(false);
    JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
    jsplistuser.setBounds(525, 25, 156, 320);

    jtextListUsers.setContentType("text/html");
    jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);


    // Box for user input
    jtextInputChat.setBounds(0, 350, 400, 50);
    jtextInputChat.setFont(font);
    jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
    final JScrollPane jtextInputChatSP = new JScrollPane(jtextInputChat);
    jtextInputChatSP.setBounds(25, 350, 650, 50);

    // Buttons for sending messages and disconnecting from server
    final JButton sendMessage = new JButton("Send Message");
    sendMessage.setFont(font);
    sendMessage.setBounds(475, 410, 200, 35);

    final JButton disconnectButton = new JButton("Disconnect");
    disconnectButton.setFont(font);
    disconnectButton.setBounds(25, 410, 130, 35);

    jtextInputChat.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          sendMessage();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
          String currentMessage = jtextInputChat.getText().trim();
          jtextInputChat.setText(oldMsg);
          oldMsg = currentMessage;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          String currentMessage = jtextInputChat.getText().trim();
          jtextInputChat.setText(oldMsg);
          oldMsg = currentMessage;
        }
      }
    });

    sendMessage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        sendMessage();
      }
    });

    // Initial text for input fields
    final JTextField passwordInput = new JTextField(this.password);
    final JTextField usernameInput = new JTextField(this.name);
    final JTextField portInput = new JTextField(Integer.toString(this.PORT));
    final JTextField addressInput = new JTextField(this.serverName);
    final JButton connectButton = new JButton("Connect");

    passwordInput.getDocument().addDocumentListener(new TextListener(passwordInput, usernameInput, portInput, addressInput, connectButton));
    usernameInput.getDocument().addDocumentListener(new TextListener(passwordInput, usernameInput, portInput, addressInput, connectButton));
    portInput.getDocument().addDocumentListener(new TextListener(passwordInput, usernameInput, portInput, addressInput, connectButton));
    addressInput.getDocument().addDocumentListener(new TextListener(passwordInput, usernameInput, portInput, addressInput, connectButton));

    // Sets position and size of each field and button for signin process
    connectButton.setFont(font);
    addressInput.setBounds(25, 380, 135, 40);
    usernameInput.setBounds(375, 380, 135, 40);
    portInput.setBounds(200, 380, 135, 40);
    passwordInput.setBounds(550, 380, 135, 40); 
    connectButton.setBounds(575, 425, 100, 40);

    jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
    jtextListUsers.setBackground(Color.LIGHT_GRAY);

    jfr.add(connectButton);
    jfr.add(jtextFilDiscuSP);
    jfr.add(jtextListUsers);
    jfr.add(passwordInput);
    jfr.add(usernameInput);
    jfr.add(portInput);
    jfr.add(addressInput);
    jfr.setVisible(true);

    connectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          password = passwordInput.getText();
          name = usernameInput.getText();
          String port = portInput.getText();
          serverName = addressInput.getText();
          PORT = Integer.parseInt(port);

          appendToPane(jtextFilDiscu, "<span>Connecting to " + serverName + " on port " + PORT + "...</span>");
          server = new Socket(serverName, PORT);

          appendToPane(jtextFilDiscu, "<span>Connected to " +
              server.getRemoteSocketAddress() + "as " + name +"</span>");

          input = new BufferedReader(new InputStreamReader(server.getInputStream()));
          output = new PrintWriter(server.getOutputStream(), true);

          // Sends username and password to server for signin process
          output.println(name);
          output.println(password);

          // Changes buttons and fields when user signs in
          read = new Read();
          read.start();
          jfr.remove(passwordInput);
          jfr.remove(usernameInput);
          jfr.remove(portInput);
          jfr.remove(addressInput);
          jfr.remove(connectButton);
          jfr.add(sendMessage);
          jfr.add(jtextInputChatSP);
          jfr.add(jtextListUsers);
          jfr.add(disconnectButton);
          jfr.revalidate();
          jfr.repaint();
          jtextFilDiscu.setBackground(Color.WHITE);
          jtextListUsers.setBackground(Color.WHITE);
        } catch (Exception ex) {
          appendToPane(jtextFilDiscu, "<span>Could not connect to Server</span>");
          JOptionPane.showMessageDialog(jfr, ex.getMessage());
        }
      }

    });

    disconnectButton.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent ae) {
        // When user disconnects, switch back to appropriate input fields
        jfr.add(passwordInput);
        jfr.add(usernameInput);
        jfr.add(portInput);
        jfr.add(addressInput);
        jfr.add(connectButton);
        jfr.remove(sendMessage);
        jfr.remove(jtextInputChatSP);
        jfr.remove(disconnectButton);
        jfr.revalidate();
        jfr.repaint();
        read.interrupt();
        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);
        appendToPane(jtextFilDiscu, "<span>Connection closed.</span>");
        output.close();
      }
    });

  }

  // Creates listeners for text fields in signin process
  public class TextListener implements DocumentListener{
    JTextField jtf1;
    JTextField jtf2;
    JTextField jtf3;
    JTextField jtf4;
    JButton jcbtn;

    public TextListener(JTextField jtf1, JTextField jtf2, JTextField jtf3, JTextField jtf4, JButton jcbtn){
      this.jtf1 = jtf1;
      this.jtf2 = jtf2;
      this.jtf3 = jtf3;
      this.jtf4 = jtf4;
      this.jcbtn = jcbtn;
    }

    public void changedUpdate(DocumentEvent e) {}
    
    // If any field is empty, disable button
    public void removeUpdate(DocumentEvent e) {
      if(jtf1.getText().trim().equals("") ||
          jtf2.getText().trim().equals("") ||
          jtf3.getText().trim().equals("") ||
          jtf4.getText().trim().equals("")
          ){
        jcbtn.setEnabled(false);
      }else{
        jcbtn.setEnabled(true);
      }
    }
    public void insertUpdate(DocumentEvent e) {
      if(jtf1.getText().trim().equals("") ||
          jtf2.getText().trim().equals("") ||
          jtf3.getText().trim().equals("") ||
          jtf4.getText().trim().equals("")
          ){
        jcbtn.setEnabled(false);
      }else{
        jcbtn.setEnabled(true);
      }
    }

  }

  public void sendMessage() {
    try {
      String message = jtextInputChat.getText().trim();
      if (message.equals("")) {
        return;
      }
      this.oldMsg = message;
      output.println(message);
      jtextInputChat.requestFocus();
      jtextInputChat.setText(null);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage());
      System.exit(0);
    }
  }

  public static void main(String[] args) throws Exception {
    ClientGui client = new ClientGui();
  }
  
  class Read extends Thread {
    public void run() {
      String message;
      while(!Thread.currentThread().isInterrupted()){
        try {
          message = input.readLine();
          if(message != null){
            if (message.charAt(0) == '[') {
               message = message.substring(1, message.length()-1);
               ArrayList<String> ListUser = new ArrayList<String>(
                   Arrays.asList(message.split(", "))
                   );
              jtextListUsers.setText(null);
              jtextListUsers.setText("User List: ");
              for (String user : ListUser) {
                appendToPane(jtextListUsers, user);
              }
            }else{
              appendToPane(jtextFilDiscu, message);
            }
          }
        }
        catch (IOException ex) {
          System.err.println("Failed to parse incoming message");
        }
      }
    }
  }
  
  private void appendToPane(JTextPane tp, String msg){
    HTMLDocument doc = (HTMLDocument)tp.getDocument();
    HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
    try {
      editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
      tp.setCaretPosition(doc.getLength());
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}