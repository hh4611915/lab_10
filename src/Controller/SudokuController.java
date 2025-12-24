package src.Controller;

import src.Backend.GameGenerator;
import src.Backend.SudokuSolver;
import src.Backend.SudokuVerifier;
import src.Model.Catalog;
import src.Model.DifficultyEnum;
import src.Model.SudokuBoard;
import src.Storage.NotFoundException;
import src.Storage.Storage;

import java.io.IOException;

public class SudokuController implements Viewable {
    private Storage storage;

    public SudokuController() {
        this.storage = Storage.getInstance();
    }

    @Override
    public Catalog getCatalog() {
        return storage.getCatalogInfo();
    }

    @Override
    public int[][] getGame(String levelName) throws NotFoundException {
        return storage.loadGame(levelName.toLowerCase());
    }

    public int[][] getInitialGame(String levelName) {
        return storage.loadRawBoard(levelName.toLowerCase());
    }

    @Override
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

    @Override
    public void logUserAction(int r, int c, int val, int prev) throws IOException {
        storage.logMove(r, c, val, prev);
    }

    public void createIncompleteGame(int[][] board) {
        try {
            storage.saveGame("incomplete", board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String verifyGame(int[][] game) {
        return SudokuVerifier.verify(game);
    }

    @Override
    public int[] undo() throws IOException {
        String logLine = storage.undoLastLog();
        if (logLine == null) return null;

        String[] parts = logLine.split(",");
        int r = Integer.parseInt(parts[0]);
        int c = Integer.parseInt(parts[1]);
        int prev = Integer.parseInt(parts[3]);

        return new int[]{r, c, prev};
    }

    @Override
    public int[][] solveGame(int[][] game) throws Exception {
        int[][] result = new SudokuSolver().solve(game);
        if (result == null) {
            throw new RuntimeException("No solution found.");
        }
        return result;
    }

    @Override
    public void onGameWon(String difficulty) {
        if (difficulty != null && !difficulty.equals("incomplete")) {
            storage.deleteGame(difficulty.toLowerCase());
        }
        storage.deleteGame("incomplete");
    }
}