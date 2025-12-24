package src.Backend;

import src.BackendIterator.ConcreteSudokuAggregation;
import src.BackendIterator.SudokuAggregation;
import src.BackendIterator.SudokuIterator;

import java.util.List;

public class SolverWorker extends Thread {
    private final int[][] board;
    private final List<Integer> emptyIndices;
    private final long startIdx;
    private final long endIdx;
    private final boolean[] solutionFound;
    private final int[][] finalResultContainer;

    public SolverWorker(int[][] board, List<Integer> emptyIndices, long start, long end,
                        boolean[] solutionFound, int[][] finalResultContainer) {
        this.board = board;
        this.emptyIndices = emptyIndices;
        this.startIdx = start;
        this.endIdx = end;
        this.solutionFound = solutionFound;
        this.finalResultContainer = finalResultContainer;
    }

    @Override
    public void run() {
        // USE AGGREGATION PATTERN HERE
        SudokuAggregation aggregation = new ConcreteSudokuAggregation(startIdx, endIdx, emptyIndices.size(), solutionFound);
        SudokuIterator iterator = aggregation.createIterator();

        SudokuFlyweight verifier = new SudokuFlyweight(board);

        while (iterator.hasNext()) {
            if (solutionFound[0]) return;

            int[] combination = iterator.next();

            if (verifier.isValid(emptyIndices, combination)) {
                solutionFound[0] = true;

                for(int i=0; i<emptyIndices.size(); i++) {
                    int idx = emptyIndices.get(i);
                    finalResultContainer[idx/9][idx%9] = combination[i];
                }

                for(int r=0; r<9; r++) {
                    for(int c=0; c<9; c++) {
                        if (finalResultContainer[r][c] == 0) {
                            finalResultContainer[r][c] = board[r][c];
                        }
                    }
                }
                return;
            }
        }
    }
}