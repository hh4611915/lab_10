public class SudokuBoard {
    private int[][] board;

    public SudokuBoard(int[][] board) {
        this.board = new int[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                this.board[i][j] = board[i][j];
    }

    public int[][] getBoard() {
        return board;
    }

    public void setCell(int row, int col, int value) {
        board[row][col] = value;
    }
}