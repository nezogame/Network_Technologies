package org.denys.hudymov;

// A Java program for a Server

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;
import lombok.Data;
import org.denys.hudymov.entity.Game;
import org.denys.hudymov.service.GameService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Data
@Component
public class Server {
    //initialize socket and input stream

    private final GameService gameService;
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private SimaGame simaGame = new SimaGame();
    private TournamentResults results = new TournamentResults();
    private Long gameId;
    private Optional<Game> lastGame;

    // constructor with port

    public Server(GameService gameService) {
        this.gameService = gameService;
        Scanner scanner = new Scanner(System.in);
        simaGame.initializeMiddle();
        // starts server and waits for a connection
        var gameEnded = false;
        try {
            server = new ServerSocket(5000);
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
                lastGame = gameService.findLastGame();
                if (lastGame.isPresent() && (!lastGame.get().getThird1Win() && !lastGame.get().getThird2Win())) {
                    gameId = lastGame.get().getGameId();

                    if (lastGame.get().getFirst1Win()) {
                        results.increasePlayer1Wins();
                    }
                    if (lastGame.get().getFirst2Win()) {
                        results.increasePlayer2Wins();
                    }
                    if (lastGame.get().getSecond1Win()) {
                        results.increasePlayer1Wins();
                    }
                    if (lastGame.get().getSecond2Win()) {
                        results.increasePlayer2Wins();
                    }
                    if (lastGame.get().getThird1Win()) {
                        results.increasePlayer1Wins();
                    }
                    if (lastGame.get().getThird2Win()) {
                        results.increasePlayer2Wins();
                    }
                } else {
                    gameId = gameService.saveGame(Game.builder()
                            .first1Win(false)
                            .first2Win(false)
                            .second1Win(false)
                            .second2Win(false)
                            .third1Win(false)
                            .third2Win(false).build());
                }
                // Handle the client in a new thread to allow multiple clients
                Thread clientThread = new Thread(() -> handleClient(socket, gameEnded));
                clientThread.start();

            } catch (IOException e) {
                System.err.println("Error accepting a client connection: " + e.getMessage());
            }
        }
    }

    private synchronized void handleClient(Socket socket, boolean gameEnded) {
        try (
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ) {
            Scanner scanner = new Scanner(System.in);
            StringBuilder serverResponse = new StringBuilder();
            serverResponse.append("Current Game Score are {Player1Wins: ")
                    .append(results.getPlayer1Wins())
                    .append(" |Player2Wins: ")
                    .append(results.getPlayer2Wins())
                    .append("}\n")
                    .append(simaGame.printBoard());
            if (simaGame.isPlayer1Turn()) {
                serverResponse.append("\nГравець 1 , введіть координати (рядок і стовпець):");
            } else {
                serverResponse.append("\nНаразі ходить гравець 2.");
            }
            try {
                out.writeUTF(serverResponse.toString());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            String clientResponse;
            while (true) {
                serverResponse.delete(0, serverResponse.length());
                System.out.println(simaGame.printBoard());
                Integer row = null;
                Integer col = null;

                if (simaGame.isPlayer1Turn()) {
                    clientResponse = in.readUTF();
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
                    do {
                        try {
                            row = scanner.nextInt();
                            col = scanner.nextInt();
                        } catch (InputMismatchException e) {
                            System.err.println("Invalid input format");
                            scanner.next();
                        }

                    } while (row == null);
                }
                var move = simaGame.run(row, col);
                // Process the client's move and get a response
                String processedMove = processClientMove(move);
                System.out.println(processedMove);
                if (processedMove.equals("Гравець 1 переміг!")) {
                    gameEnded = true;
                    results.increasePlayer1Wins();
                    gameService.updateGame(Game.builder()
                            .gameId(gameId)
                            .first1Win(true)
                            .first2Win(results.getPlayer2Wins() >= 1)
                            .second1Win(results.getPlayer1Wins() >= 2)
                            .second2Win(results.getPlayer2Wins() >= 2)
                            .third1Win(results.getPlayer1Wins() >= 3)
                            .third2Win(results.getPlayer2Wins() >= 3)
                            .build());
                    serverResponse.append(simaGame.printBoard())
                            .append("\n")
                            .append(processedMove);
                    if (results.getPlayer1Wins() > 2) {
                        System.out.println("Гравець 1 переміг у турнірі!");
                        out.writeUTF("Гравець 1 переміг у турнірі!");
                        break;
                    }
                    simaGame.cleanBoard();
                } else if (processedMove.equals("Гравець 2 переміг!")) {
                    gameEnded = true;
                    results.increasePlayer2Wins();
                    gameService.updateGame(Game.builder()
                            .gameId(gameId)
                            .first2Win(true)
                            .first1Win(results.getPlayer1Wins() >= 1)
                            .second2Win(results.getPlayer2Wins() >= 2)
                            .second1Win(results.getPlayer1Wins() >= 2)
                            .third2Win(results.getPlayer2Wins() >= 3)
                            .third1Win(results.getPlayer1Wins() >= 3)
                            .build());
                    serverResponse.append(simaGame.printBoard())
                            .append("\n")
                            .append(processedMove);
                    if (results.getPlayer2Wins() > 2) {
                        System.out.println("Гравець 2 переміг у турнірі!");
                        out.writeUTF("Гравець 2 переміг у турнірі!");
                        break;
                    }
                    simaGame.cleanBoard();
                }
                if (gameEnded) {
                    gameEnded = false;
                    simaGame.setPlayerMove(0);
                    System.out.println("Current Game Score are {Player1Wins: " +
                            results.getPlayer1Wins() +
                            " |Player2Wins: " +
                            results.getPlayer2Wins() +
                            "}\n");
                    serverResponse.append("\nCurrent Game Score are {Player1Wins: ")
                            .append(results.getPlayer1Wins())
                            .append(" |Player2Wins: ")
                            .append(results.getPlayer2Wins())
                            .append("}");
                    out.writeUTF(serverResponse.toString());
                }
                // Send the response back to the client
                if (simaGame.isPlayer1Turn()) {
                    System.out.println("Гравець 1 зробив хід.");
                    serverResponse.append(simaGame.printBoard())
                            .append("\n")
                            .append(processedMove)
                            .append("\nГравець 1 , введіть координати (рядок і стовпець):");
                    try {
                        out.writeUTF(serverResponse.toString());
                    } catch (IOException e) {
                        System.err.println("Client was disconnected: " + e.getMessage());
                    }
                } else {
                    if (processedMove.equals("Це недопустимий хід. Спробуйте ще раз.") ||
                            processedMove.equals("Неправильні координати. Спробуйте ще раз.") ||
                            processedMove.equals("Точки лінії повинні бути поряд.")) {
                        serverResponse.append(simaGame.printBoard())
                                .append("\nГравець 2 , зробив хід але хід некоректний.");
                    } else {
                        serverResponse.append(simaGame.printBoard())
                                .append("\nГравець 2 , зробив хід.");
                    }
                    try {
                        out.writeUTF(serverResponse.toString());
                    } catch (IOException e) {
                        System.err.println("Client was disconnected: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Client was disconnected: " + e.getMessage());
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
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
            return "Неправильні координати. Спробуйте ще раз.";
        } else if (move.equals("Player1Wins")) {
            return "Гравець 1 переміг!";
        } else if (move.equals("Player2Wins")) {
            return "Гравець 2 переміг!";
        } else if (move.equals("PlaceNear")) {
            return "Точки лінії повинні бути поряд.";
        } else {
            return ""; // Continue playing
        }
    }


    public static void main(String args[]) {

        SpringApplication.run(Server.class, args);
    }
}

