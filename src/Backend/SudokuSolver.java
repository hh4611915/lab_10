package src.Backend;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolver {

    public int[][] solve(int[][] board) {
        List<Integer> emptyIndices = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == 0) emptyIndices.add(r * 9 + c);
            }
        }

        if (emptyIndices.size() != 5) {
            throw new RuntimeException("Solver only works with exactly 5 empty cells");
        }

        boolean[] solutionFound = { false };
        int[][] resultBoard = new int[9][9];

        long totalPermutations = (long) Math.pow(9, 5);
        int numThreads = 4;
        long chunkSize = totalPermutations / numThreads;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            long start = i * chunkSize;
            long end = (i == numThreads - 1) ? totalPermutations : (start + chunkSize);

            Thread worker = new SolverWorker(board, emptyIndices, start, end, solutionFound, resultBoard);
            threads.add(worker);
            worker.start();
        }

        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        if (solutionFound[0]) {
            return resultBoard;
        } else {
            return null;
        }
    }
}