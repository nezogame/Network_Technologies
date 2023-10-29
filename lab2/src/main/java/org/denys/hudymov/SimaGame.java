package org.denys.hudymov;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;

import static java.lang.Math.abs;

public class SimaGame {
    public static void main(String[] args) {
        int size = 7; // Розмір поля
        char[][] board = new char[size][size]; // Поле гри
        Scanner scanner = new Scanner(System.in);
        boolean player1Turn = true; // Починає гравець 1
        int playerMove = 0;
        List<Pair<Integer, Integer>> prevMove = new ArrayList<>();


        // Ініціалізуємо поле
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = ' ';
            }
        }

        board[size / 2][size / 2] = 'X';
        prevMove.add(new Pair<>(size / 2, size / 2));

        while (true) {
            // Виводимо поточний стан поля гри
            printBoard(board);
            char currentPlayerSymbol = player1Turn ? 'X' : 'O';
            System.out.println("Гравець " + (player1Turn ? "1" : "2") + ", введіть координати (рядок і стовпець):");

            int row = scanner.nextInt();
            int col = scanner.nextInt();

            // Перевіряємо, чи введені координати дійсні
            if (row < 0 || row >= size || col < 0 || col >= size || board[row][col] != ' ') {
                System.out.println("Неправильні координати. Спробуйте ще раз.");
                continue;
            }
            //check weather line broken or not
            var prev = prevMove.get(prevMove.size() - 1);
            if (abs(row - prev.getKey()) > 1 || abs(col - prev.getValue()) > 1) {
                System.out.println("You should place dot near the previous.");
                continue;
            }

            // Розміщуємо відрізок на полі
            if (isValidMove(board, row, col, prevMove)) {
                prevMove.add(new Pair<>(row, col));
                board[row][col] = currentPlayerSymbol;
                playerMove++;
            } else {
                System.out.println("Це недопустимий хід. Спробуйте ще раз.");
                continue;
            }

            // Перевіряємо, чи гра закінчилася
            if (isGameOver(board, row, col, currentPlayerSymbol)) {
                printBoard(board);
                System.out.println("Гравець " + (player1Turn ? "1" : "2") + " переміг!");
                break;
            }

            // Змінюємо чергу гравця
            if (playerMove > 2) {
                player1Turn = !player1Turn;
                playerMove = 0;
            }
        }

        scanner.close();
    }

    // Метод для виводу поля гри
    public static void printBoard(char[][] board) {
        System.out.println("  ──────────────────────────────");
        for (int i = 0; i < board.length; i++) {
            System.out.print(i + ")| ");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println("\n  ──────────────────────────────");
        }
    }

    // Метод, який перевіряє, чи хід є допустимим
    public static boolean isValidMove(char[][] board, int row, int col,
                                      List<Pair<Integer, Integer>> prevMove) {
        // Перевірка, чи можна розмістити відрізок на це поле
        if (board[row][col] == ' ') {
            // Перевірка, чи не перетинається лінія
            return countIntersectingLines(board, row, col, prevMove);
        }
        return false;
    }

    public static boolean countIntersectingLines(char[][] board, int row, int col,
                                                 List<Pair<Integer, Integer>> prevMove) {
        // Перевірка, чи можна розмістити відрізок на це поле
        if (board[row][col] == ' ') {
            // Перевірка, чи не перетинається лінія
            return !intersectsLine(row, col, prevMove);
        }
        return false;
    }

    // Метод для перевірки, чи хід перетинає іншу лінію
    public static boolean intersectsLine(int row, int col,
                                         List<Pair<Integer, Integer>> prevMove) {
        var prev = prevMove.get(prevMove.size() - 1);
        // Перевірка діагоналей
        var left = prev.getKey() - row;
        var right = prev.getValue() - col;
        var firstIndex = prevMove.indexOf(new Pair<>(prev.getKey() - left, prev.getValue()));
        var secondIndex = prevMove.indexOf(new Pair<>(prev.getKey(), prev.getValue() - right));
        System.out.println("First Index: " + firstIndex + ", Second Index:" + secondIndex);
        System.out.println("left: " + (prev.getKey() - left) + ":" + prev.getValue() + ", right:" + prev.getKey() + ":" + (prev.getValue() - right));
        if (firstIndex == -1 || secondIndex == -1) {
            return false;
        }
        if (abs(firstIndex - secondIndex) == 1) {
            return true;
        }

        return false;
    }

    // Метод, який перевіряє, чи гравець переміг
    public static boolean isGameOver(char[][] board, int row, int col, char symbol) {
        int size = board.length;

        // Перевірка ламаних ліній
        int lineCount = 0;

        // Перевірка горизонталі та вертикалі
        int horizontalCount = 0;
        int verticalCount = 0;

        for (int i = 0; i < size; i++) {
            if (board[i][col] == symbol) {
                verticalCount++;
            }
            if (board[row][i] == symbol) {
                horizontalCount++;
            }
        }

        // Перевірка діагоналей
        int diagonalCount1 = 0;
        int diagonalCount2 = 0;
        for (int i = 0; i < size; i++) {
            if (board[i][i] == symbol) {
                diagonalCount1++;
            }
            if (board[i][size - 1 - i] == symbol) {
                diagonalCount2++;
            }
        }

        // Перевірка ламаних ліній
        if (horizontalCount >= 3 || verticalCount >= 3) {
            lineCount++;
        }
        if (diagonalCount1 >= 3 || diagonalCount2 >= 3) {
            lineCount++;
        }

        // Гравець переміг, якщо він утворив дві ламані лінії
        return lineCount >= 2;
    }
}
