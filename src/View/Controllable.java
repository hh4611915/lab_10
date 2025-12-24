package src.View;

public interface Controllable {
    void updateBoard(int[][] board);
    void showMessage(String message);
    void showError(String error);
}