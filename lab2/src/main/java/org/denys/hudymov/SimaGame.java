package org.denys.hudymov;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Math.abs;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SimaGame {

    private int size = 7; // Розмір поля
    private boolean player1Turn = true; // Починає гравець 1
    private int playerMove = 0;
    private char[][] board = new char[size][size]; // Поле гри
    private List<Pair<Integer, Integer>> prevMove = new ArrayList<>();

    public synchronized void initializeMiddle() {
        // Ініціалізуємо поле
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = ' ';
            }
        }
        board[size / 2][size / 2] = 'X';
        prevMove.add(new Pair<>(size / 2, size / 2));
    }

    public synchronized String run(int row, int col) {
        // Виводимо поточний стан поля гри
        printBoard();
        char currentPlayerSymbol = player1Turn ? 'X' : 'O';

        // Перевіряємо, чи введені координати дійсні
        if (row < 0 || row >= size || col < 0 || col >= size || board[row][col] != ' ') {
            return "IncorrectCoordinates";
        }
        //check weather line broken or not
        var prev = prevMove.get(prevMove.size() - 1);
        if (abs(row - prev.getKey()) > 1 || abs(col - prev.getValue()) > 1) {
            return "PlaceNear";
        }

        // Розміщуємо відрізок на полі
        if (isValidMove(row, col)) {
            prevMove.add(new Pair<>(row, col));
            board[row][col] = currentPlayerSymbol;
            playerMove++;
        } else {
            return "InvalidMove";
        }

        // Перевіряємо, чи гра закінчилася
        if (isGameOver(row, col)) {
            printBoard();
            return (player1Turn ? "Player1Wins" : "Player2Wins");
        }

        // Змінюємо чергу гравця
        if (playerMove > 2) {
            player1Turn = !player1Turn;
            playerMove = 0;
        }

        return "Continue";
    }

    // Метод для виводу поля гри
    public String printBoard() {
        StringBuilder boardBuilder = new StringBuilder();
        boardBuilder.append("  ──────────────────────────────");
        for (int i = 0; i < board.length; i++) {
            boardBuilder.append("\n");
            boardBuilder.append(i + ")| ");
            for (int j = 0; j < board[i].length; j++) {
                boardBuilder.append(board[i][j] + " | ");
            }
            boardBuilder.append("\n  ──────────────────────────────");
        }
        return boardBuilder.toString();
    }

    // Метод, який перевіряє, чи хід є допустимим
    public boolean isValidMove(int row, int col) {
        // Перевірка, чи можна розмістити відрізок на це поле
        if (board[row][col] == ' ') {
            // Перевірка, чи не перетинається лінія
            return !intersectsLine(row, col);
        }
        return false;
    }

    // Метод для перевірки, чи хід перетинає іншу лінію
    public boolean intersectsLine(int row, int col) {
        var prev = prevMove.get(prevMove.size() - 1);
        // Перевірка діагоналей
        var left = prev.getKey() - row;
        var right = prev.getValue() - col;
        var firstIndex = prevMove.indexOf(new Pair<>(prev.getKey() - left, prev.getValue()));
        var secondIndex = prevMove.indexOf(new Pair<>(prev.getKey(), prev.getValue() - right));
        if (firstIndex == -1 || secondIndex == -1) {
            return false;
        }
        if (abs(firstIndex - secondIndex) == 1) {
            return true;
        }

        return false;
    }

    // Check if all surrounding cells are occupied
    public boolean checkSurroundingCellsOccupied(int row, int col) {
        boolean leftUp = (row == 0 || col == 0) || isValidMove(row - 1, col - 1);
        boolean leftDown = (row == 6 || col == 0) || isValidMove(row + 1, col - 1);
        boolean rightDown = (row == 6 || col == 6) || isValidMove(row + 1, col + 1);
        boolean rightUp = (row == 0 || col == 6) || isValidMove(row - 1, col + 1);
        boolean up = (row == 0) || isValidMove(row - 1, col);
        boolean down = (row == 6) || isValidMove(row + 1, col);
        boolean left = (col == 0) || isValidMove(row, col - 1);
        boolean right = (col == 6) || isValidMove(row, col + 1);

        return leftUp || leftDown || rightDown || rightUp || up || down || left || right;
    }

    // Метод, який перевіряє, чи гравець переміг
    public boolean isGameOver(int row, int col) {
        return !checkSurroundingCellsOccupied(row, col);
    }
}