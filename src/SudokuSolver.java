package src;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolver {

    public int[] solve(int[][] board) {
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(i * 9 + j);
                }
            }
        }

        if (emptyCells.size() != 5) {
            throw new RuntimeException("Solver only works with exactly 5 empty cells");
        }

        int[] positions = new int[emptyCells.size()];
        for(int i=0; i<emptyCells.size(); i++) {
            positions[i] = emptyCells.get(i);
        }

        SudokuAggregation aggregation = new ConcreteSudokuAggregation();
        SudokuIterator iterator = aggregation.createIterator();
        SudokuFlyweight flyweight = new SudokuFlyweight(board);

        while (iterator.hasNext()) {
            int[] values = iterator.next();

            if (flyweight.checkCombination(values, positions)) {
                return values;
            }
        }

        throw new RuntimeException("No solution found");
    }
}