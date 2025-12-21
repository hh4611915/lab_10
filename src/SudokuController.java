package src;

import java.io.IOException;

public class SudokuController {
    private Storage storage;

    public SudokuController() {
        this.storage = new Storage();
    }

    public Catalog getCatalog() {
        return storage.getCatalogInfo();
    }

    public void generateGamesFromSource(int[][] source) throws IOException {
        try {
            SudokuBoard easy = GameGenerator.generate(source, DifficultyEnum.EASY);
            SudokuBoard medium = GameGenerator.generate(source, DifficultyEnum.MEDIUM);
            SudokuBoard hard = GameGenerator.generate(source, DifficultyEnum.HARD);

            storage.saveGame("easy", easy.getBoard());
            storage.saveGame("medium", medium.getBoard());
            storage.saveGame("hard", hard.getBoard());
        } catch (RuntimeException e) {
            throw new IOException(e.getMessage());
        }
    }

    public int[][] getGame(String levelName) {
        return storage.loadGame(levelName.toLowerCase());
    }

    // NEW: Gets the board WITHOUT replaying logs (to know what was fixed)
    public int[][] getInitialGame(String levelName) {
        return storage.loadRawBoard(levelName.toLowerCase());
    }

    public void createIncompleteGame(int[][] board) {
        try {
            storage.saveGame("incomplete", board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String verifyGame(int[][] game) {
        return SudokuVerifier.verify(game);
    }

    public void logMove(int r, int c, int val, int prev) {
        try {
            storage.logMove(r, c, val, prev);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] undo() {
        try {
            String logLine = storage.undoLastLog();
            if (logLine == null) return null;

            String[] parts = logLine.split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            int prev = Integer.parseInt(parts[3]);

            return new int[]{r, c, prev};
        } catch (IOException e) {
            return null;
        }
    }

    public int[][] solveGame(int[][] game) throws Exception {
        new SudokuSolver().solve(game);
        return game;
    }

    public void onGameWon(String difficulty) {
        if (difficulty != null && !difficulty.equals("incomplete")) {
            storage.deleteGame(difficulty.toLowerCase());
        }
        storage.deleteGame("incomplete");
    }
}