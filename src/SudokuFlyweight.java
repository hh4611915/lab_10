package src;

public class SudokuFlyweight {
    private final int[][] board;

    public SudokuFlyweight(int[][] board) {
        this.board = board;
    }

    public boolean checkCombination(int[] combination, int[] positions) {
        for (int i = 0; i < positions.length; i++) {
            int pos = positions[i];
            int row = pos / 9;
            int col = pos % 9;
            board[row][col] = combination[i];
        }

        String result = SudokuVerifier.verify(board);
        boolean isValid = result.equals("VALID");

        if (!isValid) {
            for (int i = 0; i < positions.length; i++) {
                int pos = positions[i];
                int row = pos / 9;
                int col = pos % 9;
                board[row][col] = 0;
            }
        }

        return isValid;
    }
}
