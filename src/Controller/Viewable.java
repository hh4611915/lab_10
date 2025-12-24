package src.Controller;

import src.Model.Catalog;
import src.Storage.NotFoundException;

import java.io.IOException;

public interface Viewable {
    Catalog getCatalog();
    int[][] getGame(String levelName) throws NotFoundException;
    void generateGamesFromSource(int[][] source) throws IOException;
    String verifyGame(int[][] game);
    int[][] solveGame(int[][] game) throws Exception;
    void logUserAction(int r, int c, int val, int prev) throws IOException;
    int[] undo() throws IOException;
    void onGameWon(String difficulty);
}