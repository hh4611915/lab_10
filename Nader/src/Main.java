import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        int[][] solvedBoard = readSolvedBoard("data/solved.txt");
        System.out.println("Source verification: " +
                SudokuVerifier.verify(solvedBoard));

        SudokuBoard easy =
                GameGenerator.generate(solvedBoard, DifficultyEnum.EASY);
        SudokuBoard medium =
                GameGenerator.generate(solvedBoard, DifficultyEnum.MEDIUM);
        SudokuBoard hard =
                GameGenerator.generate(solvedBoard, DifficultyEnum.HARD);

        System.out.println("Easy: " +
                SudokuVerifier.verify(easy.getBoard()));
        System.out.println("Medium: " +
                SudokuVerifier.verify(medium.getBoard()));
        System.out.println("Hard: " +
                SudokuVerifier.verify(hard.getBoard()));
    }

    private static int[][] readSolvedBoard(String path)
            throws FileNotFoundException {

        int[][] board = new int[9][9];
        Scanner sc = new Scanner(new File(path));

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                board[i][j] = sc.nextInt();

        sc.close();
        return board;
    }
}