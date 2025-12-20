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
            case MEDIUM -> 25;
            case HARD -> 20;
        };

        List<int[]> pairs = rp.generateDistinctPairs(removeCount);

        for (int[] p : pairs) {
            int index = p[0] % 81;
            int row = index / 9;
            int col = index % 9;
            board.setCell(row, col, 0);
        }

        return board;
    }
}