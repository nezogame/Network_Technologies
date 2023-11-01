package org.denys.hudymov;

// A Java program for a Server

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private SimaGame simaGame = new SimaGame();

    // constructor with port
    public Server(int port) {
        Scanner scanner = new Scanner(System.in);
        simaGame.initializeMiddle();
        // starts server and waits for a connection

        try {
            server = new ServerSocket(port);
            System.out.println("Server started");
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            return;
        }

        while (true) {
            try {
                System.out.println("Waiting for a client ...");
                Socket socket = server.accept();
                System.out.println("Client accepted");

                // Handle the client in a new thread to allow multiple clients
                Thread clientThread = new Thread(() -> handleClient(socket));
                clientThread.start();
            } catch (IOException e) {
                System.err.println("Error accepting a client connection: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket socket) {
        try (
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ) {
            Scanner scanner = new Scanner(System.in);
            StringBuilder serverResponse = new StringBuilder();
            serverResponse.append(simaGame.printBoard());
            serverResponse.append("\nГравець 1 , введіть координати (рядок і стовпець):");
            try {
                out.writeUTF(serverResponse.toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            String clientResponse;
            while (true) {
                serverResponse.delete(1,serverResponse.length());
                System.out.println(simaGame.printBoard());
                int row = 0;
                int col = 0;

                if (simaGame.isPlayer1Turn()) {
                    clientResponse = in.readUTF();
                    if (clientResponse.equals("Stop") || clientResponse.contains("переміг!")) {
                        break;
                    }
                    String[] parts = clientResponse.split(" "); // Split the string by space

                    if (parts.length == 2) {
                        try {
                            row = Integer.parseInt(parts[0]);
                            col = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input format");
                        }
                    } else {
                        System.out.println("Invalid input format");
                    }
                } else {
                    System.out.println("Гравець 2 , введіть координати (рядок і стовпець):");
                    row = scanner.nextInt();
                    col = scanner.nextInt();
                }
                var move = simaGame.run(row, col);
                // Process the client's move and get a response
                String processedMove = processClientMove(move);
                System.out.println(processedMove);
                if (processedMove.equals("Гравець 1 переміг!") || processedMove.equals("Гравець 2 переміг!")) {
                    out.writeUTF(processedMove);
                    break;
                }
                // Send the response back to the client
                if (simaGame.isPlayer1Turn()) {
                    System.out.println("Гравець 1 зробив хід.");
                    serverResponse.append(simaGame.printBoard());
                    serverResponse.append("\n").append(processedMove);
                    serverResponse.append("\nГравець 1 , введіть координати (рядок і стовпець):");
                    try {
                        out.writeUTF(serverResponse.toString());
                    } catch (IOException e) {
                        System.out.println();
                        throw new RuntimeException(e);
                    }
                }else{
                    serverResponse.append(simaGame.printBoard());
                    serverResponse.append("\nГравець 2 , зробив хід.");
                    try {
                        out.writeUTF(serverResponse.toString());
                    } catch (IOException e) {
                        System.out.println();
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        }
    }

    private String processClientMove(String move) {
        // Your move processing logic goes here
        // Check if the move is valid, if not, return an error message.
        // Check if a player wins, if so, return a win message.
        // Otherwise, return a message indicating a successful move.

        // For example:
        if (move.equals("InvalidMove")) {
            return "Це недопустимий хід. Спробуйте ще раз.";
        } else if (move.equals("IncorrectCoordinates")) {
            return " Неправильні координати. Спробуйте ще раз.";
        } else if (move.equals("Player1Wins")) {
            return "Гравець 1 переміг!";
        } else if (move.equals("Player2Wins")) {
            return "Гравець 2 переміг!";
        } else if (move.equals("PlaceNear")) {
            return "Точки лінії повинні бути поряд";
        } else {
            return ""; // Continue playing
        }
    }

    public static void main(String args[]) {
        Server server = new Server(5000);
    }
}

