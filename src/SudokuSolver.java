package src;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolver {

    public int[] solve(int[][] board) {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }

        if (emptyCells.size() != 5) {
            throw new RuntimeException("Solver only works with exactly 5 empty cells");
        }

        SudokuAggregation aggregation = new ConcreteSudokuAggregation();
        SudokuIterator iterator = aggregation.createIterator();

        while (iterator.hasNext()) {
            int[] values = iterator.next();

            for (int i = 0; i < 5; i++) {
                int[] coord = emptyCells.get(i);
                board[coord[0]][coord[1]] = values[i];
            }

            if (SudokuVerifier.verify(board).equals("VALID")) {
                return values;
            }

            for (int i = 0; i < 5; i++) {
                int[] coord = emptyCells.get(i);
                board[coord[0]][coord[1]] = 0;
            }
        }

        throw new RuntimeException("No solution found");
    }
}