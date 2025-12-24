package src.Backend;

import src.Model.DifficultyEnum;
import src.Model.SudokuBoard;

import java.util.List;

public class GameGenerator {

    public static SudokuBoard generate(int[][] solved, DifficultyEnum level) {
        if (!SudokuVerifier.verify(solved).equals("VALID")) {
            throw new RuntimeException("Source solution invalid or incomplete");
        }

        SudokuBoard board = new SudokuBoard(solved);
        RandomPairs rp = new RandomPairs();

        int removeCount = switch (level) {
            case EASY -> 10;
            case MEDIUM -> 20;
            case HARD -> 25;
        };

        List<int[]> pairs = rp.generateDistinctPairs(removeCount);

        for (int[] p : pairs) {
            board.setCell(p[0], p[1], 0);
        }

        return board;
    }
}
