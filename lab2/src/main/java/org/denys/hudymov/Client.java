package org.denys.hudymov;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    private Socket socket = null;
    private DataInputStream input = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    // constructor to put ip address and port
    public Client(String address, int port) {
        Scanner scanner = new Scanner(System.in);
        Integer row = null;
        Integer col;
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
        while (true) {
            if (line.contains("переміг у турнірі!")) {
                break;
            }
            try {
                line = in.readUTF();
                if (line.contains("Гравець 2 , зробив хід але хід некоректний.")) {
                    System.out.println(line);
                    continue;
                }
                if (line.contains("Наразі ходить гравець 2.") || line.contains("Гравець 2 , зробив хід.")) {
                    System.out.println("Зараз ходить Гравець 2.");
                    System.out.println(line);
                    continue;
                }
                if (line.contains("Гравець 1 переміг!")) {
                    System.out.println(line);
                    continue;
                }
                System.out.println(line);
                do {
                    try {
                        row = scanner.nextInt();
                        col = scanner.nextInt();
                        out.writeUTF(row + " " + col);
                    } catch (InputMismatchException e) {
                        System.err.println("Invalid input format");
                        scanner.next(); // Очистити буфер введення
                    }
                } while (row == null);
            } catch (IOException e) {
                System.err.println("Server was Stopped and You was disconnected: " + e.getMessage());
                break;
            }

        }

        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (
                IOException i) {
            System.out.println(i);
        }

    }


    public static void main(String args[]) {
        //Client client = new Client("192.168.8.249", 6666);
        Client client = new Client("127.0.0.1", 5000);
    }
}
