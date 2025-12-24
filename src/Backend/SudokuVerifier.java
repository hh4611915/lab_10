package src.Backend;
public class SudokuVerifier {

    public static String verify(int[][] board) {
        boolean incomplete = false;

        for (int i = 0; i < 9; i++) {
            boolean[] row = new boolean[10];
            boolean[] col = new boolean[10];

            for (int j = 0; j < 9; j++) {
                int r = board[i][j];
                int c = board[j][i];

                if (r == 0 || c == 0) incomplete = true;

                if (r != 0) {
                    if (row[r]) return "INVALID";
                    row[r] = true;
                }

                if (c != 0) {
                    if (col[c]) return "INVALID";
                    col[c] = true;
                }
            }
        }

        for (int br = 0; br < 9; br += 3) {
            for (int bc = 0; bc < 9; bc += 3) {
                boolean[] box = new boolean[10];
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++) {
                        int v = board[br + i][bc + j];
                        if (v == 0) incomplete = true;
                        if (v != 0) {
                            if (box[v]) return "INVALID";
                            box[v] = true;
                        }
                    }
            }
        }

        return incomplete ? "INCOMPLETE" : "VALID";
    }
}