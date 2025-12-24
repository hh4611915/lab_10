package src.View;

import src.Controller.SudokuController;
import src.Controller.Viewable;
import src.Model.Catalog;
import src.Storage.NotFoundException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SudokuGUI extends JFrame implements Controllable {

    private Viewable controller;
    private JTextField[][] cells = new JTextField[9][9];
    private JButton btnVerify, btnSolve, btnUndo;
    private int[][] previousState = new int[9][9];
    private boolean isProgrammaticUpdate = false;
    private String currentDifficulty = "";
    private SudokuController castController;

    public SudokuGUI() {
        this.castController = new SudokuController();
        this.controller = castController;

        initUI();
        startFlow();
    }

    private void initUI() {
        setTitle("Lab 10 Sudoku");
        setSize(600, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(9, 9));

        Color borderColor = Color.BLACK;
        int thick = 4;
        int thin = 1;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(new Font("SansSerif", Font.BOLD, 20));

                int top = (i % 3 == 0) ? thick : thin;
                int left = (j % 3 == 0) ? thick : thin;
                int bottom = (i == 8) ? thick : 0;
                int right = (j == 8) ? thick : 0;

                Border gridBorder = BorderFactory.createMatteBorder(top, left, bottom, right, borderColor);
                Border paddingBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                cells[i][j].setBorder(BorderFactory.createCompoundBorder(gridBorder, paddingBorder));

                final int r = i;
                final int c = j;

                cells[i][j].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        try {
                            String txt = cells[r][c].getText();
                            previousState[r][c] = txt.isEmpty() ? 0 : Integer.parseInt(txt);
                        } catch (Exception ex) {
                            previousState[r][c] = 0;
                        }
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        handleInputChange(r, c);
                    }
                });

                boardPanel.add(cells[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        btnVerify = new JButton("Verify");
        btnSolve = new JButton("Solve");
        btnUndo = new JButton("Undo");

        btnSolve.setEnabled(false);

        btnVerify.addActionListener(e -> verifyAction());
        btnSolve.addActionListener(e -> solveAction());
        btnUndo.addActionListener(e -> undoAction());

        controlPanel.add(btnVerify);
        controlPanel.add(btnSolve);
        controlPanel.add(btnUndo);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void startFlow() {
        Catalog cat = controller.getCatalog();
        try {
            if (cat.current) {
                currentDifficulty = "incomplete";
                int[][] currentBoard = controller.getGame("incomplete");
                int[][] initialBoard = castController.getInitialGame("incomplete");
                loadGameToBoard(currentBoard, initialBoard);
                showMessage("Resumed unfinished game.");
            }
            else if (cat.allModesExist) {
                askDifficultyAndLoad();
            }
            else {
                String path = JOptionPane.showInputDialog(this,
                        "No games found.\nPlease enter path to solved Sudoku file (e.g. data/solved.txt):");

                if (path != null && !path.trim().isEmpty()) {
                    int[][] source = loadSourceFile(path.trim());
                    controller.generateGamesFromSource(source);
                    showMessage("Games generated successfully!");
                    askDifficultyAndLoad();
                } else {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void askDifficultyAndLoad() throws NotFoundException {
        String[] options = {"EASY", "MEDIUM", "HARD"};
        int choice = JOptionPane.showOptionDialog(this, "Select Difficulty", "New Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice != -1) {
            currentDifficulty = options[choice];
            int[][] board = controller.getGame(options[choice]);
            castController.createIncompleteGame(board);
            loadGameToBoard(board, board);
        } else {
            System.exit(0);
        }
    }

    private void loadGameToBoard(int[][] currentBoard, int[][] initialBoard) {
        updateBoard(currentBoard);

        isProgrammaticUpdate = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (initialBoard[i][j] != 0) {
                    cells[i][j].setEditable(false);
                    cells[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    cells[i][j].setEditable(true);
                    cells[i][j].setBackground(Color.WHITE);
                }
            }
        }
        checkSolveButton();
        isProgrammaticUpdate = false;
    }

    private void loadGameToBoard(int[][] board) {
        loadGameToBoard(board, board);
    }

    private void handleInputChange(int r, int c) {
        if (isProgrammaticUpdate) return;
        checkSolveButton();
        try {
            String valTxt = cells[r][c].getText();
            int val = valTxt.isEmpty() ? 0 : Integer.parseInt(valTxt);
            int prev = previousState[r][c];
            if (val != prev) {
                controller.logUserAction(r, c, val, prev);
            }
        } catch (Exception e) { }
    }

    private void undoAction() {
        try {
            int[] move = controller.undo();
            if (move != null) {
                isProgrammaticUpdate = true;
                int r = move[0];
                int c = move[1];
                int prevVal = move[2];
                cells[r][c].setText(prevVal == 0 ? "" : String.valueOf(prevVal));
                checkSolveButton();
                isProgrammaticUpdate = false;
            } else {
                showMessage("Nothing to undo!");
            }
        } catch(IOException e) {
            showError("Undo failed");
        }
    }

    private void checkSolveButton() {
        int emptyCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].getText().trim().isEmpty()) {
                    emptyCount++;
                }
            }
        }
        btnSolve.setEnabled(emptyCount == 5);
    }

    private void verifyAction() {
        int[][] currentBoard = parseBoard();
        String result = controller.verifyGame(currentBoard);
        showMessage("Verification Result: " + result);

        if (result.equals("VALID")) {
            controller.onGameWon(currentDifficulty);
            showMessage("Game Completed! File deleted.\nExiting...");
            System.exit(0);
        }
    }

    private void solveAction() {
        try {
            int[][] currentBoard = parseBoard();
            int[][] solved = controller.solveGame(currentBoard);
            loadGameToBoard(solved);
            showMessage("Puzzle Solved!");
        } catch (Exception e) {
            showError("Solver failed: " + e.getMessage());
        }
    }

    private int[][] parseBoard() {
        int[][] temp = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String txt = cells[i][j].getText();
                try {
                    temp[i][j] = txt.isEmpty() ? 0 : Integer.parseInt(txt);
                } catch (NumberFormatException e) {
                    temp[i][j] = 0;
                }
            }
        }
        return temp;
    }

    private int[][] loadSourceFile(String path) throws Exception {
        Scanner sc = new Scanner(new File(path));
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sc.hasNextInt()) board[i][j] = sc.nextInt();
            }
        }
        sc.close();
        return board;
    }

    @Override
    public void updateBoard(int[][] board) {
        isProgrammaticUpdate = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int val = board[i][j];
                cells[i][j].setText(val != 0 ? String.valueOf(val) : "");
            }
        }
        isProgrammaticUpdate = false;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }
}