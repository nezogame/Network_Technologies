package org.denys.hudymov;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // constructor to put ip address and port
    public Client(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            // sends output to the socket
            out = new DataOutputStream(
                    socket.getOutputStream());
        } catch (UnknownHostException u) {
            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        // string to read message from input
        String line = "";

        // keep reading until "Stop" is input
        while (!line.equals("Stop")) {
            try {
                line = input.readLine();
                out.writeUTF(line);
                line = in.readUTF();
                System.out.println(line);
            } catch (IOException i) {
                System.out.println(i);
            }
        }

        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        Client client = new Client("127.0.0.1", 5000);
    }
}
