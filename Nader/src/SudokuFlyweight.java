class SudokuFlyweight {
    private int[][] board;

    public SudokuFlyweight(int[][] board) {
        this.board = board;
    }

    public boolean checkCombination(int[] combination, int[] positions) {
        for (int i = 0; i < positions.length; i++) {
            int pos = positions[i];
            int row = pos / 9;
            int col = pos % 9;
            if (board[row][col] != 0) {
                return false;
            }
        }
        return true;
    }
}
