package src;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Sudoku Application Starting ===");

        Storage storage = new Storage();
        Catalog catalog = storage.getCatalogInfo();

        if (!catalog.allModesExist) {
            System.out.println("Game files missing. Generating new games...");
            try {
                int[][] sourceBoard = readSolvedBoard("data/solved.txt");

                SudokuBoard easy = GameGenerator.generate(sourceBoard, DifficultyEnum.EASY);
                storage.saveGame("easy", easy.getBoard());

                SudokuBoard medium = GameGenerator.generate(sourceBoard, DifficultyEnum.MEDIUM);
                storage.saveGame("medium", medium.getBoard());

                SudokuBoard hard = GameGenerator.generate(sourceBoard, DifficultyEnum.HARD);
                storage.saveGame("hard", hard.getBoard());

                System.out.println("Generated and Saved Easy, Medium, and Hard games.");

            } catch (Exception e) {
                System.out.println("Error generating games (Make sure data/solved.txt exists): " + e.getMessage());
            }
        } else {
            System.out.println("All game modes are ready in storage.");
        }

        System.out.println("\n=== Testing Solver ===");
        try {
            int[][] board = storage.loadGame("easy");

            prepareBoardForSolver(board);

            System.out.println("Prepared board with 5 empty cells. Solving...");
            SudokuSolver solver = new SudokuSolver();
            int[] solution = solver.solve(board);

            System.out.print("Solution found: ");
            for(int val : solution) System.out.print(val + " ");
            System.out.println();

        } catch (Exception e) {
            System.out.println("Solver Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void prepareBoardForSolver(int[][] board) {
        int zeroCount = 0;
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                if(board[i][j] == 0) zeroCount++;

        int zerosToRemove = zeroCount - 5;
        for(int i=0; i<9 && zerosToRemove > 0; i++) {
            for(int j=0; j<9 && zerosToRemove > 0; j++) {
                if(board[i][j] == 0) {
                    board[i][j] = 1;
                    zerosToRemove--;
                }
            }
        }
    }

    private static int[][] readSolvedBoard(String path) throws java.io.FileNotFoundException {
        int[][] board = new int[9][9];
        Scanner sc = new Scanner(new File(path));
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                board[i][j] = sc.nextInt();
        sc.close();
        return board;
    }
}