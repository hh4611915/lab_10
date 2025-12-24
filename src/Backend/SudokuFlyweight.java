package src.Backend;

import java.util.List;

public class SudokuFlyweight {
    private final int[][] board;

    public SudokuFlyweight(int[][] board) {
        this.board = board;
    }

    public boolean isValid(List<Integer> emptyCellIndices, int[] values) {
        int numEmpty = emptyCellIndices.size();

        for (int i = 0; i < numEmpty; i++) {
            int idx = emptyCellIndices.get(i);
            int r = idx / 9;
            int c = idx % 9;
            int val = values[i];

            for (int k = 0; k < 9; k++) {
                if (k == c) continue;
                int currentVal = getVirtualValue(emptyCellIndices, values, r, k);
                if (currentVal == val) return false;
            }

            for (int k = 0; k < 9; k++) {
                if (k == r) continue;
                int currentVal = getVirtualValue(emptyCellIndices, values, k, c);
                if (currentVal == val) return false;
            }

            int startRow = (r / 3) * 3;
            int startCol = (c / 3) * 3;
            for (int row = startRow; row < startRow + 3; row++) {
                for (int col = startCol; col < startCol + 3; col++) {
                    if (row == r && col == c) continue;
                    int currentVal = getVirtualValue(emptyCellIndices, values, row, col);
                    if (currentVal == val) return false;
                }
            }
        }
        return true;
    }

    private int getVirtualValue(List<Integer> emptyIndices, int[] values, int r, int c) {
        int flatIndex = r * 9 + c;
        for (int i = 0; i < emptyIndices.size(); i++) {
            if (emptyIndices.get(i) == flatIndex) {
                return values[i];
            }
        }
        return board[r][c];
    }
}